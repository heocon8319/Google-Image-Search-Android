package com.dandewine.user.tocleveroad;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.squareup.picasso.Transformation;


/**
 * Created by Artem Manayenko on 30.03.2014.
 */
public class Config565Transformation implements Transformation {


    @Override
    public Bitmap transform(Bitmap source) {
        return BitmapsUtils.convertTo565(source,true);
    }

    @Override
    public String key() {
        return "Config565Transformation";
    }
}
