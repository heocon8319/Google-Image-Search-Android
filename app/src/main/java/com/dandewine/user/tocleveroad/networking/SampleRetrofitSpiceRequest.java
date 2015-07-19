package com.dandewine.user.tocleveroad.networking;

import com.dandewine.user.tocleveroad.model.GoogleSearchResponse;
import com.octo.android.robospice.request.listener.RequestCancellationListener;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

public class SampleRetrofitSpiceRequest extends RetrofitSpiceRequest<GoogleSearchResponse,GoogleSearchInterface> {

    String query;
    long startIndex;

    public SampleRetrofitSpiceRequest(String query, long startIndex) {
        super(GoogleSearchResponse.class, GoogleSearchInterface.class);
        this.query = query;
        this.startIndex = startIndex;
    }
    @Override
    public GoogleSearchResponse loadDataFromNetwork() throws Exception {
        return getService().search(query,startIndex);
    }
}
