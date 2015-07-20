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

    int requestCountFromInputFields;
    int firstVivisibleItemsGrid[] = new int[2];

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

        recyclerView = new RecyclerView(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setVerticalScrollBarEnabled(true);
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        if(imageList==null)
            imageList = new ArrayList<>();

        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ResultsImageAdapter(imageList,context);
        adapter.setOnItemClickListener(adapterListener);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new OnResultsScrollListener());
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
        if(isFromInputField) {//if user want to find example ronaldo and some later messi, image list with ronaldo will be clear
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

           /*try {
                 spiceManager.getFromCache(GoogleSearchResponse.class, "ronaldo",DurationInMillis.ONE_WEEK,new RequestImageListener());
          }catch(Exception e){
                 e.printStackTrace();
          }*/
        request=null;

    }

    public void updateSearchResults(@NonNull ArrayList<GoogleImage> images){
        int newSize = images.size();
        imageList.addAll(images);
        adapter.notifyItemRangeChanged(0,newSize);
    }
    private class OnResultsScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            firstVivisibleItemsGrid = mLayoutManager.findFirstVisibleItemPositions(firstVivisibleItemsGrid);

            if(!MainActivity.isListView) {
                if (isEnd()) {
                    loadMoreImages();
                }
            }else{
                if(isEnd()){
                    loadMoreImages();
                }
            }
        }
        private boolean isEnd(){
            return !recyclerView.canScrollVertically(1) && firstVivisibleItemsGrid[0] != 0;
        }
        private void loadMoreImages(){
            sendRequest(searchQuery, false);
            Log.d("myTag", "LAST-------HERE------PAGE "+ nextPage);
        }
    }
    private final class RequestImageListener implements RequestListener<GoogleSearchResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(context, "Error" +spiceException.getMessage(), Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onRequestSuccess(GoogleSearchResponse s) {
            Toast.makeText(context,"success",Toast.LENGTH_SHORT).show();
            nextPage = s.queries.getNextPage().startIndex;
            Log.d("myTag","nextPage "+ nextPage);
            updateSearchResults(s.items);
        }
    }

}
