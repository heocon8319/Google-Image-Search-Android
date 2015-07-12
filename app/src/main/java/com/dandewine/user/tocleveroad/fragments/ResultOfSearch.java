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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dandewine.user.tocleveroad.GalleryActivity;
import com.dandewine.user.tocleveroad.adapters.ImageAdapter;
import com.dandewine.user.tocleveroad.MainActivity;
import com.dandewine.user.tocleveroad.R;
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
    private SampleRetrofitSpiceRequest request;
    private SpiceManager spiceManager = new SpiceManager(SampleRetrofitSpiceService.class);
    ArrayList<GoogleImage> imageList;
    MainActivity context;
    //==============================================================================================

    //ACTIVITY LIFECYCLE

    //==============================================================================================

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.result_fragment,container,false);
        ButterKnife.inject(this,v);
        setRetainInstance(true);
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
        recyclerView.addOnScrollListener(new ScrollListener());
        linearLayout.addView(recyclerView);
        return linearLayout;
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
    public void sendRequest(String query){
        request = new SampleRetrofitSpiceRequest(query, 1);
        spiceManager.execute(request, query, DurationInMillis.ONE_WEEK, new RequestImageListener());
       /* try {
            spiceManager.getFromCache(GoogleSearchResponse.class, "food",DurationInMillis.ONE_WEEK,new RequestImageListener());
        }catch(Exception e){
            e.printStackTrace();
        }*/
    }
    public void updateSearchResults(ArrayList<GoogleImage> images){
        int oldSize = imageList.size();
        int newSearch = images.size();
        imageList.addAll(images);
        adapter.notifyItemRangeChanged(0,oldSize+newSearch);
    }
    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        }
    }
    private class RequestImageListener implements RequestListener<GoogleSearchResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onRequestSuccess(GoogleSearchResponse s) {
            Toast.makeText(context,"success",Toast.LENGTH_SHORT).show();
            ResultOfSearch results =(ResultOfSearch)context.getSupportFragmentManager().findFragmentByTag(context.pagerAdapter.getTag(0));
            GalleryActivity.images = s.items;
            results.updateSearchResults(s.items);
        }
    }
}
