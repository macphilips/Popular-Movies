package com.rmhub.popularmovies.helper;

/**
 * Created by MOROLANI on 4/8/2017
 * <p>
 * owm
 * .
 */

public class Video {
    private Video() {
    }

    public static class Query implements com.rmhub.popularmovies.helper.Query {
       private final String query;

        public Query(String query) {
            this.query = query;
        }

        @Override
        public String query() {
            return query;
        }

        @Override
        public String resultCallBackName() {
            return Result.class.getName();
        }
    }

    public static class Result extends ResultCallback {

        @Override
        public void onFetchResult(String result) {

        }
    }
}
