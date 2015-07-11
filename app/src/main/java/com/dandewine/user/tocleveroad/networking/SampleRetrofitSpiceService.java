package com.dandewine.user.tocleveroad.networking;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.octo.android.robospice.retrofit.RetrofitJackson2SpiceService;



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
