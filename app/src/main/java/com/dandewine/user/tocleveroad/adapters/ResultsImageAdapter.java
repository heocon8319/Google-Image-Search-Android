package com.dandewine.user.tocleveroad.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.dandewine.user.tocleveroad.GoogleImageSearcher;
import com.dandewine.user.tocleveroad.MainActivity;
import com.dandewine.user.tocleveroad.R;
import com.dandewine.user.tocleveroad.db.ToFavouriteService;
import com.dandewine.user.tocleveroad.fragments.Favourite;
import com.dandewine.user.tocleveroad.model.GoogleImage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


import butterknife.ButterKnife;
import butterknife.InjectView;

public class ResultsImageAdapter extends RecyclerView.Adapter<ResultsImageAdapter.ViewHolder> {
    ArrayList<GoogleImage> imageList;
    ArrayList<String> savedURLs;
    Context context;
    OnItemClickListener mItemClickListener;
    ImageLoader loader;

    public ResultsImageAdapter(ArrayList<GoogleImage> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
        loader = ImageLoader.getInstance();
        loader.init(ImageLoaderConfiguration.createDefault(context));
        savedURLs =  ((GoogleImageSearcher)context.getApplicationContext()).getUrlList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.result_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final GoogleImage outerImg = imageList.get(i);
        final GoogleImage.Image image = imageList.get(i).getImage();
        final String link = image.getThumbnailLink();
        Picasso.with(context).load(link).config(Bitmap.Config.RGB_565)
                .resize((int) image.getThumbnailWidth(), (int) image.getThumbnailHeight()).centerInside().into(viewHolder.image);
       /* final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)//it's important for recycling view
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        loader.displayImage(link,viewHolder.image,options);*/
        viewHolder.text.setText(outerImg.getTitle());
        viewHolder.favouriteCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//remove from favourite
                Log.d("myTag", "on remove click");
                v.setVisibility(View.GONE);
                viewHolder.favouriteUncheck.setVisibility(View.VISIBLE);
                outerImg.setIsFavourite(false);
                goToService(outerImg.getLink());
                removeImageFromExternal(outerImg.getTitle());
                updateFavouriteAdapter(outerImg.getTitle(),null,false);
                removeSavedUrl(outerImg.getLink(), true);
            }
        });
        viewHolder.favouriteUncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//add to favourite
                checkFavouriteImage(outerImg,image);
                savedURLs.add(outerImg.getLink());
                Log.d("myTag", "on add click");
                v.setVisibility(View.GONE);
                viewHolder.favouriteCheck.setVisibility(View.VISIBLE);
                outerImg.setIsFavourite(true);
                goToService(outerImg.getTitle(), outerImg.getLink());
            }
        });
        if(savedURLs!=null) {//favourite checks for items that already has been added to favourite
            for (String url : savedURLs)
                if (TextUtils.equals(outerImg.getLink(), url))
                    outerImg.setIsFavourite(true);
        }
        if(outerImg.isFavourite()) {
            viewHolder.favouriteCheck.setVisibility(View.VISIBLE);
            viewHolder.favouriteUncheck.setVisibility(View.GONE);
        }else{
            viewHolder.favouriteCheck.setVisibility(View.GONE);
            viewHolder.favouriteUncheck.setVisibility(View.VISIBLE);
        }

    }
    //get the original image size, save to SD,
    private void checkFavouriteImage(final GoogleImage outerImg,final GoogleImage.Image image){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap;
                    int width = (int) (image.getWidth() / 2);
                    int height = (int) (image.getHeight() / 2);
                    // TODO: 15.07.2015 this is no working with some images, I tried Glide,Picasso,UIL,Ion.
                    bitmap = Picasso.with(context).load(outerImg.getLink()).memoryPolicy(MemoryPolicy.NO_STORE)
                            .config(Bitmap.Config.RGB_565)
                            .resize(width, height)
                            .onlyScaleDown()
                            .get();
                    Log.d("myTag", String.format("imageSize = %sx%s, byteCount = %s", bitmap.getWidth(), bitmap.getHeight(), bitmap.getByteCount() / 1024));
                    saveToExternal(bitmap, outerImg.getTitle());
                    updateFavouriteAdapter(outerImg.getTitle(), outerImg.getLink(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void goToService(String title,String url){
        Intent intent = new Intent(context, ToFavouriteService.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("action", ToFavouriteService.ADD_IMAGE);
        context.startService(intent);
    }
    //this method for unchecking heartIcon  if bookmark was chosen early, and user delete image
    // from bookmark page
    public void removeSavedUrl(String url,boolean isInternalAccess){
        savedURLs.remove(url);//remove from itemList
        if(!isInternalAccess) {
            for (int i = 0; i <imageList.size() ; i++) {
                if(TextUtils.equals(imageList.get(i).getLink(),url)) {
                    imageList.get(i).setIsFavourite(false);//uncheck from pojo
                    this.notifyItemChanged(i);
                }
            }

        }
    }
    //delete image from DB
    private void goToService(String url){
        Intent intent = new Intent(context, ToFavouriteService.class);
        intent.putExtra("url", url);
        intent.putExtra("action", ToFavouriteService.DELETE_BY_URL);
        context.startService(intent);
    }
    //save image to SD
    private void saveToExternal(Bitmap bitmap,String fileName){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root+"/ImageSearcherCache");
        myDir.mkdir();
        File checkFile = new File(myDir,fileName+".png");
        if(checkFile.exists()){
            checkFile = null;
            checkFile = new File(myDir,fileName+"(1).png");
        }
        if (myDir.isDirectory() && myDir.exists()) {
            try {
                FileOutputStream out = new FileOutputStream(checkFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Log.d("myTag","write file to external");
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
    //memory log for testing memory usage
    private void logMem(){
        Log.d("myTag",String.format("Total memory = %s",Runtime.getRuntime().totalMemory()/1024));
    }
    //update adapter that handle favorite images, if third parameter true it means that in favorite adapter will be added image
    private void updateFavouriteAdapter(String title, String url, boolean isAdd){
        MainActivity activity = (MainActivity)context;
        Favourite fragment = (Favourite)activity.getSupportFragmentManager().findFragmentByTag(activity.pagerAdapter.getTag(1));
        if(isAdd)
            fragment.addItemFromExternal(title, url);
        else
            fragment.removeItemFromExternal(title);
    }
    private boolean removeImageFromExternal(String name) {
        File file;
        if(!name.contains(".png"))
            file = new File(Environment.getExternalStorageDirectory()+"/ImageSearcherCache/",name+".png");
        else
            file = new File(Environment.getExternalStorageDirectory()+"/ImageSearcherCache/",name);
        boolean isDelete = file.delete();
        Log.d("myTag","is delete = "+isDelete);
        return isDelete;
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.imageView)ImageView image;
        @InjectView(R.id.heart_uncheck) ImageView favouriteUncheck;
        @InjectView(R.id.heart_check)ImageView favouriteCheck;
        @InjectView(R.id.placeholder)LinearLayout placeHolder;
        @InjectView(R.id.text_title)TextView text;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this,v);
            placeHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener!=null)
                mItemClickListener.OnItemClick(v,getAdapterPosition());
        }
    }
    public interface OnItemClickListener{
        void OnItemClick(View v, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

}
