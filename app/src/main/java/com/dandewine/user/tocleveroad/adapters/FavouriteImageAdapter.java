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
import android.widget.TextView;

import com.dandewine.user.tocleveroad.MainActivity;
import com.dandewine.user.tocleveroad.R;
import com.dandewine.user.tocleveroad.db.MyContentProvider;
import com.dandewine.user.tocleveroad.db.ToFavouriteService;
import com.dandewine.user.tocleveroad.fragments.ResultOfSearch;
import com.dandewine.user.tocleveroad.other.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FavouriteImageAdapter extends RecyclerView.Adapter<FavouriteImageAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> pathList;
    private ArrayList<String> titles;
    private boolean fromCache = false;
    private ResultOfSearch resultFragment;

    public FavouriteImageAdapter(Context context, File[] files) {
        this.context = context;
        titles = new ArrayList<>();
        pathList = new ArrayList<>();
        for(File f:files) {
            pathList.add(Utils.concat("file:",f.getAbsolutePath()));
            titles.add(f.getName());
        }
        fromCache = true;
    }
    public FavouriteImageAdapter(Context context, ArrayList<String> linkList,ArrayList<String> titles) {
        this.context = context;
        this.pathList = linkList;
        this.titles = titles;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.result_item, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d("myTag","onBindViewHolder, FavouriteAdapter");
        holder.unFavouriteImage.setVisibility(View.GONE);
        holder.favouriteImage.setVisibility(View.VISIBLE);
        if(fromCache)
            Picasso.with(context).load(pathList.get(position))
                    .resizeDimen(R.dimen.large_width, R.dimen.large_height)
                    .config(Bitmap.Config.RGB_565).centerInside().into(holder.image);
        holder.title.setText(titles.get(position));
        holder.favouriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeHeartImage(holder, false);
                removeFromDB(pathList.get(position));
               boolean deleteFromSD = removeImageFromExternal(titles.get(position));
                Log.d("myTag","delete from SD = "+deleteFromSD);

                changeIconOnResults(position);
                removeItem(position);

            }
        });
        holder.unFavouriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               changeHeartImage(holder,true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.imageView)ImageView image;
        @InjectView(R.id.heart_check)ImageView favouriteImage;
        @InjectView(R.id.heart_uncheck) ImageView unFavouriteImage;
       @InjectView(R.id.text_title) TextView title;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }
    private void changeHeartImage(ViewHolder holder,boolean flag){
        if(flag){
            holder.unFavouriteImage.setVisibility(View.GONE);
            holder.favouriteImage.setVisibility(View.VISIBLE);
        }else{
            holder.favouriteImage.setVisibility(View.GONE);
            holder.unFavouriteImage.setVisibility(View.VISIBLE);
        }
    }
    private boolean removeImageFromExternal(String name) {
        File file;
        if(!name.contains(".png"))
             file = new File(Environment.getExternalStorageDirectory()+"/ImageSearcherCache/",name+".png");
        else
             file = new File(Environment.getExternalStorageDirectory()+"/ImageSearcherCache/",name);
        return file.delete();
    }
    public void addItem(String title,String url,int index){
        titles.add(title);
        pathList.add(url);
        notifyItemInserted(index);
        notifyItemRangeChanged(0, getItemCount());
    }
    public void removeItem(int position){
        titles.remove(position);
        pathList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0,getItemCount());
    }
    private void changeIconOnResults(int position){
        MainActivity activity = (MainActivity)context;
        resultFragment = (ResultOfSearch)(activity.getSupportFragmentManager().findFragmentByTag(activity.pagerAdapter.getTag(0)));
        resultFragment.adapter.removeSavedUrl(pathList.get(position), false);
    }
    private void removeFromDB(String url){
        Intent intent = new Intent(context, ToFavouriteService.class);
        intent.putExtra("action",ToFavouriteService.DELETE_BY_URL);
        intent.putExtra("url",url);
        context.startService(intent);
    }
    public void remove(String name){
        for (int i = 0; i < titles.size(); i++) {
            if(TextUtils.equals(titles.get(i), name)) {
                removeItem(i);
            }
        }
    }


}
