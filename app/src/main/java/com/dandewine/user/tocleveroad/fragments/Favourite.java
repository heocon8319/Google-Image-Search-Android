package com.dandewine.user.tocleveroad.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dandewine.user.tocleveroad.R;
import com.dandewine.user.tocleveroad.adapters.FavouriteImageAdapter;
import com.dandewine.user.tocleveroad.db.MyContentProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
    public FavouriteImageAdapter adapter;
    public RecyclerView mRecyclerView;
    public StaggeredGridLayoutManager mLayoutManager;
    File[] files;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        ButterKnife.inject(this, linearLayout);
        mLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView = new RecyclerView(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);
        File dir = new File(Environment.getExternalStorageDirectory()+"/ImageSearcherCache");
        files = dir.listFiles();
        if(files!=null)
            initAdapterFromCache(files);
        else
            initAdapterWithURL();
        linearLayout.addView(mRecyclerView);
        return linearLayout;
    }
    private void initAdapterFromCache(File[] files){
        adapter = new FavouriteImageAdapter(getActivity(), files);
        mRecyclerView.setAdapter(adapter);
    }
    private void initAdapterWithURL(){
        Cursor cursor = getActivity().getContentResolver().query(MyContentProvider.IMAGE_CONTENT_URI,new String[]{MyContentProvider.IMAGE_TITLE,MyContentProvider.IMAGE_URL},null,null,null);
        if(cursor!=null) {
            ArrayList<String> linkList = new ArrayList<>();
            ArrayList<String> titles = new ArrayList<>();
            while (cursor.moveToNext()) {
                linkList.add(cursor.getString(1));
                titles.add(cursor.getString(0));
            }
            adapter = new FavouriteImageAdapter(getActivity(),linkList,titles);
            cursor.close();
        }
    }
    public void addItemFromExternal(String title, String url){
        adapter.addItem(title,url,adapter.getItemCount());
    }
    public void removeItemFromExternal(String name){
        adapter.remove(name);
    }
}
