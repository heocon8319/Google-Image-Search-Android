package com.dandewine.user.tocleveroad.networking;

import com.dandewine.user.tocleveroad.model.GoogleSearchResponse;
import com.dandewine.user.tocleveroad.other.Utils;

import retrofit.http.GET;
import retrofit.http.Query;

public interface GoogleSearchInterface {
    public static final String BASE_URL = "https://www.googleapis.com/customsearch";
    public static final String API_KEY = "AIzaSyCCuxxVLzm2sZP-adhRNYKeSck1mMMgsAM";
    public static final String CUSTOM_SEARCH_ID = "001734592082236324715:sob9rqk49yg";
    public static final String SEARCH_TYPE_IMAGE = "image";
    static final String FILTER = "&fields=queries(nextPage(startIndex,count),request(startIndex,count)),searchInformation(totalResults),items(title,link,displayLink,mime," +
            "image)";
    static final String QUERY = "/v1?key="+API_KEY+
                                "&cx="+CUSTOM_SEARCH_ID+
                                "&searchType="+SEARCH_TYPE_IMAGE+FILTER;

    @GET(QUERY)
    public GoogleSearchResponse search(@Query("q") String query,
                                       @Query("start") long startIndex);
    @GET(QUERY)
    public GoogleSearchResponse search(@Query("q") String query,
                                       @Query("start") long startIndex,
                                       @Query("searchType") String searchType,
                                       @Query("limit") String limit,
                                       @Query("offset") String offset);
}
