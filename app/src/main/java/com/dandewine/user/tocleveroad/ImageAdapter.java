package com.dandewine.user.tocleveroad;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dandewine.user.tocleveroad.model.GoogleImage;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    ArrayList<GoogleImage> imageList;
    Context context;
    //ImageLoader imageLoader=ImageLoader.getInstance(); //for UIL

    public ImageAdapter(ArrayList<GoogleImage> imageList,Context context) {
        this.imageList = imageList;
        this.context = context;
     //imageLoader.init(ImageLoaderConfiguration.createDefault(context)); //for UIL

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        GoogleImage outerImg = imageList.get(i);
        GoogleImage.Image image = imageList.get(i).image;
        String link = image.thumbnailLink;
       // String link = "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSMp3Zb-s_O38HlY4x9xPI0k0cJ8_DtEH4zJ4mKCt_4sGapVOOYrw";
        int height = (int)image.thumbnailHeight;
        int width = (int)image.thumbnailWidth;
       Picasso.with(context)
                .load(link)
                .resize(width*2,height*2)
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
