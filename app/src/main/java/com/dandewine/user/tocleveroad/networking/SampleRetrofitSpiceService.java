package com.dandewine.user.tocleveroad.networking;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import retrofit.RestAdapter;


public class SampleRetrofitSpiceService extends RetrofitGsonSpiceService {
    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(GoogleSearchInterface.class);
    }
    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        return new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setConverter(getConverter())
                .setEndpoint(getServerUrl());
    }

    @Override
    protected String getServerUrl() {
        return GoogleSearchInterface.BASE_URL;
    }
}
