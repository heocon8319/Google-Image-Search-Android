package com.dandewine.user.tocleveroad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

public class BitmapsUtils {
    public static Bitmap convertToCircle(Bitmap source, boolean recycleSource) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (recycleSource)
            source.recycle();

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    public static Bitmap convertTo565(Bitmap source, boolean recycleSource) {
        Bitmap resultBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(source, 0, 0, paint);
        if (recycleSource)
            source.recycle();
        return resultBitmap;
    }

    public static Bitmap getFittedBitmap(Bitmap source, ImageView imageView, boolean recycleSource) {
        int bitmapWidth = source.getWidth();
        int bitmapHeight = source.getHeight();
        int newWidth = imageView.getWidth();
        int newHeight = (int) Math.floor((double) bitmapHeight * ((double) newWidth / (double) bitmapWidth));
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
        if (recycleSource)
            source.recycle();
        return scaledBitmap;
    }
    private static Bitmap decode(Bitmap bitmap,int reqWidth,int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap resultBitmap = bitmap;
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculate(options, reqWidth, reqHeight);
        options.inBitmap = resultBitmap;
        options.inJustDecodeBounds = false;
        return resultBitmap;
    }
    private static int calculate(BitmapFactory.Options options,int reqWidth,int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height>reqHeight||width>reqWidth){
            final int halfHeight = height/2;
            final int halfWidth = width/2;
            while((halfHeight/inSampleSize)>reqHeight &&
                    (halfWidth/inSampleSize)>reqWidth){
                inSampleSize*=2;
            }
        }
        return inSampleSize;

    }
}
