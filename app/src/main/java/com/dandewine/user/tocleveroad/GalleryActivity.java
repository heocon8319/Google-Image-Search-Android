package com.dandewine.user.tocleveroad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dandewine.user.tocleveroad.model.GoogleImage;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GalleryActivity extends AppCompatActivity {
    private GalleryPagerAdapter _adapter;
    public  ArrayList<GoogleImage> images;


    @InjectView(R.id.pager) ViewPager _pager;
    @InjectView(R.id.thumbnails) LinearLayout _thumbnails;
    @InjectView(R.id.btn_close) ImageButton _closeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_fragment);
        ButterKnife.inject(this);

        images = getIntent().getParcelableArrayListExtra("images");
        _adapter = new GalleryPagerAdapter(this);
        _pager.setAdapter(_adapter);
        _pager.setOffscreenPageLimit(10); // how many images to load into memory

        _closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        int currentItem = getIntent().getIntExtra("position",-1);
        if(currentItem!=-1) _pager.setCurrentItem(currentItem);

    }
    class GalleryPagerAdapter extends PagerAdapter {

        Context _context;
        LayoutInflater _inflater;
        private ImageLoader imageLoader;

        public GalleryPagerAdapter(Context context) {
            _context = context;
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = _inflater.inflate(R.layout.pager_gallery_item, container, false);
            container.addView(itemView);

            // Get the border size to show around each image
            int borderSize = _thumbnails.getPaddingTop();

            // Get the size of the actual thumbnail image
            int thumbnailSize = ((FrameLayout.LayoutParams)
                    _pager.getLayoutParams()).bottomMargin - (borderSize*2);

            // Set the thumbnail layout parameters. Adjust as required
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
            params.setMargins(0, 0, borderSize, 0);

            // You could also set like so to remove borders
            //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
            //        ViewGroup.LayoutParams.WRAP_CONTENT,
            //        ViewGroup.LayoutParams.WRAP_CONTENT);

            final ImageView thumbView = new ImageView(_context);
            thumbView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbView.setLayoutParams(params);
            thumbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _pager.setCurrentItem(position);
                }
            });
            _thumbnails.addView(thumbView);

            final ImageView imageView = ButterKnife.findById(itemView, R.id.image);
            //final SubsamplingScaleImageView imageView = ButterKnife.findById(itemView, R.id.image);
            // Asynchronously load the image and set the thumbnail and pager view
            Picasso.with(_context).load(images.get(position).getLink()).transform(new Config565Transformation()).into(imageView);
            Picasso.with(_context).load(images.get(position).getImage().getThumbnailLink()).into(thumbView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

}
