package com.dandewine.user.tocleveroad.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dandewine.user.tocleveroad.Config565Transformation;
import com.dandewine.user.tocleveroad.GoogleImageSearcher;
import com.dandewine.user.tocleveroad.R;
import com.dandewine.user.tocleveroad.ToFavouriteService;
import com.dandewine.user.tocleveroad.model.GoogleImage;
import com.dandewine.user.tocleveroad.model.GoogleSearchResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    ArrayList<GoogleImage> imageList;
    Context context;
    OnItemClickListener mItemClickListener;


    public ImageAdapter(ArrayList<GoogleImage> imageList,Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.result_item,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        GoogleImageSearcher app = (GoogleImageSearcher)context.getApplicationContext();
        final GoogleImage outerImg = imageList.get(i);
        final GoogleImage.Image image = imageList.get(i).getImage();
        String link = image.getThumbnailLink();
        int height = (int)image.getThumbnailHeight();
        int width = (int)image.getThumbnailWidth();
        Picasso.with(context).load(link).resize(width * 2, height * 2).transform(new Config565Transformation()).into(viewHolder.image);
        viewHolder.text.setText(outerImg.getTitle());
        viewHolder.favouriteCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//remove from favourite
                Log.d("myTag","on remove click");
                v.setVisibility(View.GONE);
                viewHolder.favouriteUncheck.setVisibility(View.VISIBLE);
                outerImg.setIsFavourite(false);
                goToService(outerImg.getLink());
            }
        });
        viewHolder.favouriteUncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//add to favourite
                Log.d("myTag", "on add click");
                v.setVisibility(View.GONE);
                viewHolder.favouriteCheck.setVisibility(View.VISIBLE);
                outerImg.setIsFavourite(true);
                goToService(outerImg.getTitle(), outerImg.getLink());
            }
        });
        if(app.getUrlList()!=null) {
            for (String url : app.getUrlList())
                if (TextUtils.equals(outerImg.getLink(), url))
                    outerImg.setIsFavourite(true);
        }
        if(outerImg.isFavourite()) {
            viewHolder.favouriteCheck.setVisibility(View.VISIBLE);
            viewHolder.favouriteUncheck.setVisibility(View.GONE);
        }
    }
    private void goToService(String title,String url){
        Intent intent = new Intent(context, ToFavouriteService.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("action", ToFavouriteService.ADD_IMAGE);
        context.startService(intent);
    }
    private void goToService(String url){
        Intent intent = new Intent(context, ToFavouriteService.class);
        intent.putExtra("url", url);
        intent.putExtra("action", ToFavouriteService.DELETE_BY_URL);
        context.startService(intent);
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
        ImageView image,favouriteUncheck,favouriteCheck;
        LinearLayout placeHolder,placeNameHolder;
        TextView text;
        public ViewHolder(View v) {
            super(v);
            image = ButterKnife.findById(v,R.id.imageView);
            text = ButterKnife.findById(v,R.id.text_title);
            favouriteUncheck = ButterKnife.findById(v,R.id.heart_uncheck);
            favouriteCheck = ButterKnife.findById(v,R.id.heart_check);
            placeHolder = ButterKnife.findById(v,R.id.placeholder);
            placeNameHolder = ButterKnife.findById(v,R.id.placeNameHolder);
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
