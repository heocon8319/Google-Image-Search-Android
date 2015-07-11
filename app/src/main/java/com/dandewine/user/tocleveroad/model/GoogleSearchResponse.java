package com.dandewine.user.tocleveroad.model;

import java.util.ArrayList;

public class GoogleSearchResponse {
    public ArrayList<GoogleImage> items;
    public GoogleQueries queries;
    public GoogleSearchInformation searchInformation;

    public static class GoogleQueries {
        public GooglePage[] nextPage = new GooglePage[1];
        public GooglePage[] request = new GooglePage[1];

        public static class GooglePage {
            public int startIndex;
            public int count;
        }

        public GooglePage getNextPage() {
            return nextPage[0];
        }

        public GooglePage getRequest() {
            return request[0];
        }
    }

    public static class GoogleSearchInformation {
        public long totalResults;
    }
}
