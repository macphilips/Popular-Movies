package com.rmhub.popularmovies.model;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rmhub.popularmovies.helper.MovieQuery;
import com.rmhub.popularmovies.helper.ResultHandler;
import com.rmhub.popularmovies.util.ProviderUtil;

import java.util.ArrayList;

/**
 * Created by MOROLANI on 4/8/2017
 * <p>
 * owm
 * .
 */

public class Review {
    private Review() {
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


        @SerializedName("total_results")
        @Expose
        private int totalResult;

        @SerializedName("total_pages")
        @Expose
        private int totalPages;

        @SerializedName("page")
        @Expose
        private int page;

        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("results")
        @Expose
        private ArrayList<ReviewDetail> details;


        @Override
        public void saveToDatabase(Context context, MovieDetail detail) {
            if (details != null && !details.isEmpty()) {
                Log.d(getClass().getSimpleName(), "Insert in review to database");
                ProviderUtil.insertReview(context, detail, details);
            }
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

        public ArrayList<ReviewDetail> getDetails() {
            return details;
        }

        public void setDetails(ArrayList<ReviewDetail> details) {
            this.details = details;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "totalResult=" + totalResult +
                    ", totalPages=" + totalPages +
                    ", page=" + page +
                    ", id=" + id +
                    ", details=" + details +
                    '}';
        }

        @Override
        public void loadFromDB(Context ctx, MovieDetail detail) {
            details = ProviderUtil.getReviews(ctx, detail);
            totalPages = -1;
            totalResult = details.size();
        }
    }
}
