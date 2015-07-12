package com.dandewine.user.tocleveroad.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dandewine.user.tocleveroad.GalleryActivity;
import com.dandewine.user.tocleveroad.MainActivity;
import com.dandewine.user.tocleveroad.R;
import com.dandewine.user.tocleveroad.model.GoogleImage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    ArrayList<GoogleImage> imageList;
    Context context;

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
        GoogleImage outerImg = imageList.get(i);
        GoogleImage.Image image = imageList.get(i).image;
        String link = image.thumbnailLink;
       // String link = "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSMp3Zb-s_O38HlY4x9xPI0k0cJ8_DtEH4zJ4mKCt_4sGapVOOYrw";
        int height = (int)image.thumbnailHeight;
        int width = (int)image.thumbnailWidth;
       Picasso.with(context)
                .load(link)
               .resize(width * 2, height * 2)
               .into(viewHolder.image);
        viewHolder.text.setText(outerImg.title);
        viewHolder.favouriteUncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                viewHolder.favouriteCheck.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.favouriteCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                viewHolder.favouriteUncheck.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra("position",i);
               // intent.putExtra("images",imageList);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image,favouriteUncheck,favouriteCheck;
        TextView text;
        public ViewHolder(View v) {
            super(v);
            image = ButterKnife.findById(v,R.id.imageView);
            text = ButterKnife.findById(v,R.id.text_title);
            favouriteUncheck = ButterKnife.findById(v,R.id.heart_uncheck);
            favouriteCheck = ButterKnife.findById(v,R.id.heart_check);
        }
    }
}
