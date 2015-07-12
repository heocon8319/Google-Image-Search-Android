package com.dandewine.user.tocleveroad;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;


/**
 * Created by Artem Manayenko on 30.03.2014.
 */
public class PicassoRoundTransformation implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        return BitmapsUtils.convertToCircle(source, true);
    }

    @Override
    public String key() {
        return "circle";
    }
}
