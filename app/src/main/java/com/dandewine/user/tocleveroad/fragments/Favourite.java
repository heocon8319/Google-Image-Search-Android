package com.dandewine.user.tocleveroad.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dandewine.user.tocleveroad.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Favourite extends Fragment {
    static Favourite resultFragment;
    public static Favourite getInstance(){
        if(resultFragment==null) {
            resultFragment = new Favourite();
            return resultFragment;
        }else
            return resultFragment;

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favourite_fragment,container,false);
        ButterKnife.inject(this,v);
        return v;
    }
}
