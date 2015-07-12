package com.dandewine.user.tocleveroad.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dandewine.user.tocleveroad.R;

import butterknife.ButterKnife;

public class FavouritePageAdapter extends RecyclerView.Adapter<FavouritePageAdapter.ViewHolder> {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView text;

        public ViewHolder(View v) {
            super(v);
            imageView = ButterKnife.findById(v, R.id.favourite_image);
            text = ButterKnife.findById(v,R.id.favourite_image_title);
        }
    }
}
