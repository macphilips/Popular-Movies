package com.rmhub.popularmovies.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class Movies {
    private Movies() {
    }

    public static class Query implements com.rmhub.popularmovies.helper.Query {
        private final String url;

        public Query(String s) {
            this.url = s;
        }

        @Override
        public String query() {
            return url;
        }
    }

    public static class Result implements ResultCallback {

        private List<MovieDetails> movieList;
        private String statusDesc = "";
        private int total_count;
        private int total_page;
        private int status;
        private int nextPage;

        public int getTotalCounts() {
            return total_count;
        }

        public void setTotalResult(int total_count) {
            this.total_count = total_count;
        }

        public int getTotalPages() {
            return total_page;
        }

        public void setTotalPages(int total_page) {
            this.total_page = total_page;
        }

        @Override
        public void onFetchResult(String result) {
            ParseResult.parseMovieResult(result, this);

            if (movieList == null) {
                movieList = new ArrayList<>();
            }
        }

        public List<MovieDetails> getMovieList() {
            return movieList;
        }

        public void setMovieList(List<MovieDetails> movieList) {
            this.movieList = movieList;
        }

        public String getStatusDesc() {
            return statusDesc;
        }

        public void setStatusDesc(String statusDesc) {
            this.statusDesc = statusDesc;
        }

        @NetworkStatus.Status
        public int getStatus() {
            return status;
        }

        public void setStatus(@NetworkStatus.Status int status) {
            this.status = status;
        }

        public int getNextPage() {
            return nextPage;
        }

        public void setNextPage(int nextPage) {
            this.nextPage = nextPage + 1;
        }
    }

}
