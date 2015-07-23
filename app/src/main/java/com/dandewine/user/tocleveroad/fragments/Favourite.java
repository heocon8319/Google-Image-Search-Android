package com.dandewine.user.tocleveroad.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dandewine.user.tocleveroad.GalleryActivity;
import com.dandewine.user.tocleveroad.MainActivity;
import com.dandewine.user.tocleveroad.adapters.FavouriteImageAdapter;
import com.dandewine.user.tocleveroad.db.MyContentProvider;
import com.dandewine.user.tocleveroad.other.Utils;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;

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
    private int firstVivisibleItemsGrid[] = new int[2];
    private ArrayList<File> files;
    private File dir;
    private MainActivity context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        ButterKnife.inject(this, linearLayout);
        context = (MainActivity)getActivity();
        setRetainInstance(true);

        mLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView = new RecyclerView(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new OnFavouriteScrollListener());



        dir = new File(Environment.getExternalStorageDirectory()+"/ImageSearcherCache");
        files = new ArrayList<>();
        for (int i = 0; i < 10 ; i++) {

            if(i>=dir.listFiles().length)
                break;
           files.add(dir.listFiles()[i]);
        }
        if(files!=null)
            initAdapterFromCache(files);
        else
            initAdapterWithURL();
        linearLayout.addView(mRecyclerView);
        return linearLayout;
    }
    FavouriteImageAdapter.OnItemClickListener onItemClickListener = new FavouriteImageAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int position) {
            Intent intent = new Intent(getActivity(), GalleryActivity.class);
            intent.putExtra("position",position);
            intent.putExtra("flag",true);
            startActivity(intent);
        }
    };
    private void initAdapterFromCache(ArrayList<File> files){
        adapter = new FavouriteImageAdapter(getActivity(), files);
        Log.d("myTag","from Cache");
        adapter.setOnItemClickListener(onItemClickListener);
        mRecyclerView.setAdapter(adapter);
    }
    public void toggle(int flag){
        if(flag>0){
            mLayoutManager.setSpanCount(1);
        }else{
            mLayoutManager.setSpanCount(2);
        }
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
            Log.d("myTag","from URL");
            adapter = new FavouriteImageAdapter(getActivity(),linkList,titles);
            adapter.setOnItemClickListener(onItemClickListener);
            cursor.close();
        }
    }
    public void addItemFromExternal(String title, String url){
        adapter.addItem(title,url,adapter.getItemCount());
    }
    public void removeItemFromExternal(String name){
        adapter.remove(name);
    }

    class OnFavouriteScrollListener extends RecyclerView.OnScrollListener{
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            firstVivisibleItemsGrid = mLayoutManager.findFirstVisibleItemPositions(firstVivisibleItemsGrid);
            if(!context.resultsAsListview){
                if (isEnd()) loadMoreImages();
            }
            else if(isEnd()){
                loadMoreImages();
            }
        }
        private boolean isEnd(){
            return !mRecyclerView.canScrollVertically(1) && firstVivisibleItemsGrid[0] != 0;
        }
        private void loadMoreImages(){
            String lastName = files.get(files.size()-1).getName();
            int size = dir.listFiles().length;
            if(size>adapter.getItemCount()) {//if on SD more files than have adapter
                for (int i = size - 1; i >= 0; i--) {
                    if (TextUtils.equals(dir.listFiles()[i].getName(), lastName)) //last file name on adapter == last file name on SD
                        break;
                    File f = dir.listFiles()[i];
                    files.add(f);
                    adapter.addItem(f.getName(), Utils.concat("file:",f.getAbsolutePath()), i);
                }
                adapter.notifyItemRangeChanged(0, files.size());
            }
        }

    }
}
