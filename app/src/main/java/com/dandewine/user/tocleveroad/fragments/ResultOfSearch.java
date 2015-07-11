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
import com.dandewine.user.tocleveroad.MainActivity;
import com.dandewine.user.tocleveroad.R;
import com.dandewine.user.tocleveroad.model.GoogleImage;

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
   public static ImageAdapter adapter;
    ArrayList<GoogleImage> imageList;
    MainActivity context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.result_fragment,container,false);
        ButterKnife.inject(this,v);
        context = (MainActivity)getActivity();
        final LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        recyclerView = new RecyclerView(getActivity());
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        if(imageList==null)
            imageList = new ArrayList<>();
      /*  for (int i = 0; i <10 ; i++) {//for testing
            imageList.add(new GoogleImage());
        }*/
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ImageAdapter(imageList,context);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
        linearLayout.addView(recyclerView);
        return linearLayout;
    }
    private View getRecyclerView(){
        final LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        recyclerView = new RecyclerView(getActivity());
        recyclerView.setHasFixedSize(false);
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        if(imageList==null)
            imageList = new ArrayList<>();
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ImageAdapter(imageList,context);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
        linearLayout.addView(recyclerView);
        return linearLayout;
    }
    public void updateSearchResults(ArrayList<GoogleImage> images){
        int oldSize = imageList.size();
        int newSearch = images.size();
        imageList.addAll(images);
        adapter.notifyItemRangeChanged(0,oldSize+newSearch);
    }
}
