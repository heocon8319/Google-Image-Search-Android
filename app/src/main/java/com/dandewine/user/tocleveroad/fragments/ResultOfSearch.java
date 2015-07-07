package com.dandewine.user.tocleveroad.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dandewine.user.tocleveroad.ImageAdapter;
import com.dandewine.user.tocleveroad.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ResultOfSearch extends Fragment {
    private static ResultOfSearch resultFramgent;
    public static ResultOfSearch getInstance(){
        if(resultFramgent==null){
            resultFramgent = new ResultOfSearch();
            return resultFramgent;
        }else
            return resultFramgent;
    }

    public RecyclerView recyclerView;
    public StaggeredGridLayoutManager mLayoutManager;
    ImageAdapter adapter;
    ArrayList<ImageView> imageList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.result_fragment,container,false);
        ButterKnife.inject(this,v);

        final LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        recyclerView = new RecyclerView(getActivity());
        recyclerView.setHasFixedSize(false);
        imageList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            imageList.add(new ImageView(getActivity()));
        }
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ImageAdapter(imageList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
        linearLayout.addView(recyclerView);
        return linearLayout;
    }
}
