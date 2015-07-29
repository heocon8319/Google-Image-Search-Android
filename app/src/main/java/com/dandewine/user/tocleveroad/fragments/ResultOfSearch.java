package com.dandewine.user.tocleveroad.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.dandewine.user.tocleveroad.GalleryActivity;
import com.dandewine.user.tocleveroad.R;
import com.dandewine.user.tocleveroad.adapters.ResultsImageAdapter;
import com.dandewine.user.tocleveroad.MainActivity;
import com.dandewine.user.tocleveroad.model.GoogleImage;
import com.dandewine.user.tocleveroad.model.GoogleSearchResponse;
import com.dandewine.user.tocleveroad.networking.SampleRetrofitSpiceRequest;
import com.dandewine.user.tocleveroad.networking.SampleRetrofitSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ResultOfSearch extends Fragment {
    //singletone
    private static ResultOfSearch resultFramgent;
    public static ResultOfSearch getInstance(){
        if(resultFramgent==null){
            resultFramgent = new ResultOfSearch();
            return resultFramgent;
        }else
            return resultFramgent;
    }
    //rest client
    private SampleRetrofitSpiceRequest request;
    private SpiceManager spiceManager = new SpiceManager(SampleRetrofitSpiceService.class);
    public String searchQuery; int nextPage=1;
    //context
    public RecyclerView recyclerView;
    public StaggeredGridLayoutManager mLayoutManager;
    public ResultsImageAdapter adapter;
    public ArrayList<GoogleImage> imageList;
    public MainActivity context;
    OnResultsScrollListener onResultsScrollListener;
    int requestCountFromInputFields;
    int firstVisibleItemsGrid[] = new int[2];

    //==============================================================================================

    //ACTIVITY LIFECYCLE

    //==============================================================================================

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        ButterKnife.inject(this, linearLayout);
        setRetainInstance(true);
        context = (MainActivity)getActivity();
        setRetainInstance(true);
        recyclerView = (RecyclerView)LayoutInflater.from(getActivity()).inflate(R.layout.recyclerview,container,false);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        if(imageList==null)
            imageList = new ArrayList<>();

        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ResultsImageAdapter(imageList,context);
        adapter.setOnItemClickListener(adapterListener);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
         onResultsScrollListener = new OnResultsScrollListener(mLayoutManager);
        recyclerView.addOnScrollListener(onResultsScrollListener);
        linearLayout.addView(recyclerView);

        return linearLayout;
    }
    ResultsImageAdapter.OnItemClickListener adapterListener = new ResultsImageAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int position) {
            Log.d("myTag",""+imageList.get(position).getLink());
            Intent intent = new Intent(getActivity(),GalleryActivity.class);
            intent.putExtra("position",position);
            intent.putExtra("flag",false);
            intent.putParcelableArrayListExtra("images",imageList);
            startActivity(intent);
        }
    };

    /**
     * This method change count of span of staggered layout, if positive value to Grid,
     * else to Listview
     * */
    public void toggle(int i){
        if(i>0){
            mLayoutManager.setSpanCount(1);
        }else{
            mLayoutManager.setSpanCount(2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(context);
    }
    @Override
    public void onStop() {
        super.onStop();
        spiceManager.shouldStop();
    }
    //==============================================================================================

    //HANDLING REQUEST FROM GOOGLE SEARCH API

    //==============================================================================================
    public void sendRequest(@NonNull String query,boolean isFromInputField){
        if(isFromInputField) {//if user want to find example ronaldo and some later messi, image list with ronaldo will be cleared
            requestCountFromInputFields++;
            if(requestCountFromInputFields>=2 && !TextUtils.equals(searchQuery,query))
                imageList.clear();
        }

        request = new SampleRetrofitSpiceRequest(query, nextPage);
        if(TextUtils.equals(query,searchQuery) && nextPage>10)
            spiceManager.execute(request, query+String.format("(%s)",nextPage), DurationInMillis.ONE_WEEK, new RequestImageListener());
        else
            spiceManager.execute(request, query, DurationInMillis.ONE_WEEK, new RequestImageListener());
        searchQuery = query;
        nextPage+=10;
        //for debug
       /*    try {
                 spiceManager.getFromCache(GoogleSearchResponse.class, "klitschko",DurationInMillis.ONE_WEEK,new RequestImageListener());
          }catch(Exception e){
                 e.printStackTrace();
          }*/
        request=null;

    }

    @Override
    public void onResume() {
        super.onResume();
        onResultsScrollListener.reset(0,true);
    }

    public void updateSearchResults(@NonNull ArrayList<GoogleImage> images){
        int newSize = images.size();
        int oldSize = imageList.size();
        imageList.addAll(images);
        adapter.notifyItemRangeChanged(0,newSize+oldSize);
    }
    private class OnResultsScrollListener extends RecyclerView.OnScrollListener {

        private int previousTotal = 0; // The total number of items in the dataset after the last load
        private boolean loading = true; // True if we are still waiting for the last set of data to load.
        private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
        int visibleItemCount, totalItemCount;

        private StaggeredGridLayoutManager mLayoutManager;

        public OnResultsScrollListener(StaggeredGridLayoutManager mLayoutManager) {
            this.mLayoutManager = mLayoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            firstVisibleItemsGrid = mLayoutManager.findFirstVisibleItemPositions(firstVisibleItemsGrid);
            if(loading){
                if(totalItemCount>previousTotal){
                    loading=false;
                    previousTotal=totalItemCount;
                }
            }
            if(!loading && (totalItemCount-firstVisibleItemsGrid[0])<=(firstVisibleItemsGrid[0]+visibleThreshold)){
                loadMoreImages();
                loading=true;
            }

        }
        public void reset(int previousTotal, boolean loading) {
            this.previousTotal = previousTotal;
            this.loading = loading;
        }
        private boolean isEnd(){
            return !recyclerView.canScrollVertically(1) && firstVisibleItemsGrid[0] != 0;
        }
        private void loadMoreImages(){
            sendRequest(searchQuery, false);
            Log.d("myTag", "LAST-------HERE------PAGE "+ nextPage);
        }
    }
    private final class RequestImageListener implements RequestListener<GoogleSearchResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(context, "Sorry, cannot load images :(", Toast.LENGTH_SHORT).show();
            Log.d("myTag",spiceException.getMessage());
        }
        @Override
        public void onRequestSuccess(GoogleSearchResponse s) {
            nextPage = s.queries.getNextPage().startIndex;
            Log.d("myTag","nextPage "+ nextPage);
            updateSearchResults(s.items);
        }
    }

}
