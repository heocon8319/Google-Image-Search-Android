package com.dandewine.user.tocleveroad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
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


import com.dandewine.user.tocleveroad.model.GoogleImage;
import com.dandewine.user.tocleveroad.other.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GalleryActivity extends AppCompatActivity {
    private GalleryPagerAdapter _adapter;
    public  ArrayList<GoogleImage> images;
    public ArrayList<File> files;
    boolean fromCache;
    ImageLoader imageLoader;

    @InjectView(R.id.pager) ViewPager _pager;
    @InjectView(R.id.thumbnails) LinearLayout _thumbnails;
    @InjectView(R.id.btn_close) ImageButton _closeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_fragment);
        ButterKnife.inject(this);

        fromCache = getIntent().getBooleanExtra("flag", false);
        if(!fromCache)
            images = getIntent().getParcelableArrayListExtra("images");
        else {
            if(files==null)
                files = new ArrayList<>();
            File dir = new File(Utils.concat(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
            if(dir.exists())
                Collections.addAll(files, dir.listFiles());
        }

        _adapter = new GalleryPagerAdapter(this);
        _pager.setAdapter(_adapter);
        _pager.setOffscreenPageLimit(1); // how many images to load into memory

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
        Picasso picasso;
        DisplayImageOptions options;
        ArrayList<String> thumbList;

        public GalleryPagerAdapter(Context context) {
            _context = context;
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            picasso = Picasso.with(context);
            thumbList = new ArrayList<>();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            options = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY).cacheOnDisk(true).cacheInMemory(true).build();
        }

        @Override
        public int getCount() {
           return !fromCache?images.size():files.size();
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
           // final SubsamplingScaleImageView imageView = ButterKnife.findById(itemView, R.id.image);
            // Asynchronously load the image and set the thumbnail and pager view
            if(!fromCache) {
                getImageFromNetwork(images.get(position).getLink(),imageView,options);
            }
            else
                Picasso.with(_context).load(files.get(position)).config(Bitmap.Config.RGB_565).resizeDimen(R.dimen.large_width,R.dimen.large_height).centerInside().into(imageView);
          //thumbnails
            if(!fromCache) {
                if(!thumbList.contains(images.get(position).getImage().getThumbnailLink())) {
                    Picasso.with(_context).load(images.get(position).getImage().getThumbnailLink()).into(thumbView);
                    thumbList.add(images.get(position).getImage().getThumbnailLink());
                }
            }
            else {
                if(!thumbList.contains(files.get(position).getAbsolutePath())) {
                    Picasso.with(_context).load(files.get(position)).config(Bitmap.Config.RGB_565).into(thumbView);
                    thumbList.add(files.get(position).getAbsolutePath());
                }
            }

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
        private void logMem(){
            Log.d("myTagg", String.format("Total memory = %s", Runtime.getRuntime().totalMemory() / 1024));
        }
    }
    private void getImageFromNetwork(String url, final ImageView imageView,DisplayImageOptions options){
        final ImageAware imageAware = new ImageViewAware(imageView,true);
        imageLoader.displayImage(url,imageAware,options,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                Log.d("myTag", String.format("imageSize = %sx%s, byteCount = %s", loadedImage.getWidth(), loadedImage.getHeight(), loadedImage.getByteCount() / 1024));
                imageAware.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                imageView.setImageResource(R.mipmap.cant_find);
            }
        });
    }

}
