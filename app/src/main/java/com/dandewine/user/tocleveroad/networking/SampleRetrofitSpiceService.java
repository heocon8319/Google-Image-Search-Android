package com.dandewine.user.tocleveroad.networking;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;



public class SampleRetrofitSpiceService extends RetrofitGsonSpiceService {
    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(GoogleSearchInterface.class);
    }

    @Override
    protected String getServerUrl() {
        return GoogleSearchInterface.BASE_URL;
    }
}
