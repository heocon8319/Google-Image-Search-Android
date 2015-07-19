package com.dandewine.user.tocleveroad.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utils {

    private  static StringBuilder builder = new StringBuilder();

    public static String concat(Object...rows){
        builder.setLength(0);
        for (Object o:rows)
            builder.append(o.toString());
        return builder.toString();
    }
    public static Bitmap decode(Bitmap bitmap,int reqWidth,int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = calculate(options,reqWidth,reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return options.inBitmap = bitmap;
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
