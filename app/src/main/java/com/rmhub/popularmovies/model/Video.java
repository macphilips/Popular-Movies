package com.rmhub.popularmovies.model;

import android.os.Bundle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rmhub.popularmovies.helper.ParseResult;
import com.rmhub.popularmovies.helper.MovieQuery;
import com.rmhub.popularmovies.helper.ResultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOROLANI on 4/8/2017
 * <p>
 * owm
 * .
 */

public class Video {
    private Video() {
    }

    public static class Query implements MovieQuery {
        private final Bundle query;

        public Query(Bundle query) {
            this.query = query;
        }

        @Override
        public Bundle queryBundle() {
            return query;
        }
    }


    public static class Result extends ResultHandler {


        @SerializedName("id")
        @Expose
        private int nextPage;

        @SerializedName("results")
        @Expose
        private List<VideoDetail> videoDetails = new ArrayList<>();
        private int totalResult;
        private int totalPages;
        private int page;

        public List<VideoDetail> getVideoDetails() {
            return videoDetails;
        }

        public void setVideoDetails(List<VideoDetail> videoDetails) {
            this.videoDetails = videoDetails;
        }

        @Override
        public void onFetchResult(String result) {
            videoDetails = ParseResult.parseVideo(result, this);
        }

        public int getTotalResult() {
            return totalResult;
        }

        public void setTotalResult(int totalResult) {
            this.totalResult = totalResult;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }
}
