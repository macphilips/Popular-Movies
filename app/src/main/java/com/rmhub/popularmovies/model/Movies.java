package com.rmhub.popularmovies.model;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rmhub.popularmovies.helper.ParseResult;
import com.rmhub.popularmovies.helper.MovieQuery;
import com.rmhub.popularmovies.helper.ResultHandler;
import com.rmhub.popularmovies.util.NetworkStatus;
import com.rmhub.popularmovies.util.ProviderUtil;

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

    public static class Query implements MovieQuery {
        private final Bundle url;

        public Query(Bundle s) {
            this.url = s;
        }

        @Override
        public Bundle queryBundle() {
            return url;
        }

    }

    public static class Result extends ResultHandler {

        @SerializedName("results")
        @Expose
        private List<MovieDetail> movieList;

        @SerializedName("total_results")
        @Expose
        private int total_count;

        @SerializedName("total_pages")
        @Expose
        private int total_page;

        @SerializedName("page")
        @Expose
        private int currentPage;

        private String statusDesc = "";

        private int status;

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

        @Override
        public void saveToDatabase(Context context) {
            if (movieList != null) {
                Log.d(getClass().getSimpleName(), "insert movies into database");
                ProviderUtil.insertMovies(context, movieList);
                Log.v(getClass().getSimpleName(), "" + ProviderUtil.getMovies(context).size());
            }
        }

        @Override
        public void saveToDatabase(Context context, MovieDetail moveID) {
            super.saveToDatabase(context, moveID);
            if (movieList != null) {
                ProviderUtil.insertRecommendation(context, moveID, movieList);
            }
        }

        public List<MovieDetail> getMovieList() {
            return movieList;
        }

        public void setMovieList(List<MovieDetail> movieList) {
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

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage + 1;
        }
    }

}
