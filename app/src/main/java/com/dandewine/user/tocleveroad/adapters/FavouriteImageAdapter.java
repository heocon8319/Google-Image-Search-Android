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

import com.dandewine.user.tocleveroad.MainActivity;
import com.dandewine.user.tocleveroad.R;
import com.dandewine.user.tocleveroad.db.FavouriteService;
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
    public OnItemClickListener onItemClickListener;

    public FavouriteImageAdapter(Context context, ArrayList<File> files) {
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
        Log.d("myTag", "onBindViewHolder, FavouriteAdapter");
        holder.unFavouriteImage.setVisibility(View.GONE);
        holder.favouriteImage.setVisibility(View.VISIBLE);
        if(fromCache)
            Picasso.with(context).load(pathList.get(position))
                    .resizeDimen(R.dimen.large_width, R.dimen.large_height)
                    .onlyScaleDown()
                    .config(Bitmap.Config.RGB_565).centerInside().into(holder.image);
        holder.title.setText(titles.get(position));
        holder.favouriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeHeartImage(holder, false);
                removeFromDB(titles.get(position));
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @InjectView(R.id.placeholder) LinearLayout placeHolder;
        @InjectView(R.id.imageView)ImageView image;
        @InjectView(R.id.heart_check)ImageView favouriteImage;
        @InjectView(R.id.heart_uncheck) ImageView unFavouriteImage;
       @InjectView(R.id.text_title) TextView title;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
            placeHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener!=null)
                onItemClickListener.OnItemClick(v,getAdapterPosition());
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
             file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+name+".png");
        else
             file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+name);
        return file.delete();
    }
    public void addItem(String title,String url,int index){
        if(fromCache && !url.contains("file:")){
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),Utils.concat(title,".png"));
            if(file.exists())
                pathList.add("file:"+file.getAbsolutePath());
        }else
             pathList.add(url);
        titles.add(title);
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
        resultFragment = ResultOfSearch.getInstance();
        resultFragment.adapter.removeSavedUrl(titles.get(position), false);
    }
    private void removeFromDB(String title){
        Intent intent = new Intent(context, FavouriteService.class);
        intent.putExtra("action", FavouriteService.DELETE_BY_TITLE);
        intent.putExtra("title",title);
        context.startService(intent);
    }
    public void remove(String name){
        for (int i = 0; i < titles.size(); i++) {
            if(TextUtils.equals(titles.get(i), name)) {
                removeItem(i);
            }
        }
    }
    public interface OnItemClickListener{
        void OnItemClick(View v,int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
