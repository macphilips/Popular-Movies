package com.rmhub.popularmovies.model;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rmhub.popularmovies.helper.MovieQuery;
import com.rmhub.popularmovies.helper.ParseResult;
import com.rmhub.popularmovies.helper.ResultHandler;
import com.rmhub.popularmovies.util.ProviderUtil;

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
        private int id;

        @SerializedName("results")
        @Expose
        private List<VideoDetail> videoDetails = new ArrayList<>();

        @Override
        public String toString() {
            return "Result{" +
                    "id=" + id +
                    ", videoDetails=" + videoDetails +
                    '}';
        }

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

        @Override
        public void saveToDatabase(Context context, MovieDetail moveID) {
            ProviderUtil.insertVideos(context,moveID,videoDetails);
        }

        @Override
        public void loadFromDB(Context ctx, MovieDetail detail) {
            videoDetails = ProviderUtil.getVideos(ctx, detail);
        }
    }
}
