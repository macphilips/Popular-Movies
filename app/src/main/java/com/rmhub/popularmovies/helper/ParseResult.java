package com.rmhub.popularmovies.helper;

import com.rmhub.popularmovies.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class ParseResult {
    public static List<MovieDetails> parseMovieList(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            if (obj.has("results")) {
                List<MovieDetails> movieList = new ArrayList<>();
                JSONArray jsMovieList = obj.getJSONArray("results");
                for (int i = 0, n = jsMovieList.length(); i < n; i++) {
                    movieList.add(parseMovie(jsMovieList.getJSONObject(i)));
                }
                return movieList;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void parseMovie(String result) {
        try {
            parseMovie(new JSONObject(result));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static MovieDetails parseMovie(JSONObject obj)
            throws JSONException {
        MovieDetails movieDetails = new MovieDetails();
        movieDetails.setCategory(NetworkUtil.movie_category);
        if (obj.has("adult")) {
            movieDetails.setAdult(obj.getBoolean("adult"));
        }
        if (obj.has("backdrop_path")) {
            movieDetails.setBackdrop_path(obj.getString("backdrop_path"));
        }
        if (obj.has("belongs_to_collection")) {
            movieDetails.setBelongs_to_collection(obj.getString("poster_path"));
        }
        if (obj.has("budget")) {
            movieDetails.setBudget(obj.getLong("poster_path"));
        }
        if (obj.has("genres")) {
            movieDetails.setReviewDetails(parseReview(obj.getJSONArray("genres")));
        }
        if (obj.has("homepage")) {
            movieDetails.setHomepage(obj.getString("homepage"));
        }
        if (obj.has("id")) {
            movieDetails.setMovieID(obj.getInt("id"));
        }
        if (obj.has("imdb_id")) {
            movieDetails.setImdb_id(obj.getString("imdb_id"));
        }
        if (obj.has("original_language")) {
            movieDetails.setOriginal_language(obj.getString("original_language"));
        }
        if (obj.has("original_title")) {
            movieDetails.setOriginal_title("original_title");
        }
        if (obj.has("overview")) {
            movieDetails.setOverview(obj.getString("overview"));
        }
        if (obj.has("popularity")) {
            movieDetails.setPopularity(obj.getDouble("popularity"));
        }
        if (obj.has("poster_path")) {
            movieDetails.setPoster_path(obj.getString("poster_path"));
        }
        if (obj.has("release_date")) {
            movieDetails.setRelease_date(obj.getString("release_date"));
        }

        if (obj.has("revenue")) {
            movieDetails.setRevenue(obj.getLong("revenue"));
        }
        if (obj.has("runtime")) {
            movieDetails.setRuntime(obj.getInt("runtime"));
        }
        if (obj.has("status")) {
            movieDetails.setStatus(obj.getString("status"));
        }
        if (obj.has("tagline")) {
            movieDetails.setTagline(obj.getString("tagline"));
        }
        if (obj.has("title")) {
            movieDetails.setTitle(obj.getString("title"));
        }
        if (obj.has("video")) {
            movieDetails.setVideo(obj.getBoolean("video"));
        }
        if (obj.has("vote_average")) {
            movieDetails.setVote_average(obj.getDouble("vote_average"));
        }
        if (obj.has("vote_count")) {
            movieDetails.setVote_count(obj.getInt("vote_count"));
        }
        if (obj.has("")) {

        }
        return movieDetails;
    }

    private static List<ReviewDetails> parseReview(JSONArray genres)
            throws JSONException {
        List<ReviewDetails> reviewDetailsList = new ArrayList<>();
        for (int i = 0, n = genres.length(); i < n; i++) {
            reviewDetailsList.add(parseReview(genres.getJSONObject(i)));
        }

        return reviewDetailsList;
    }

    private static ReviewDetails parseReview(JSONObject genres)
            throws JSONException {
        ReviewDetails reviewDetail = new ReviewDetails();
        if (genres.has("id")) {
            reviewDetail.setId(genres.getString("id"));
        }
        if (genres.has("author")) {
            reviewDetail.setAuthor(genres.getString("name"));
        }
        if (genres.has("content")) {
            reviewDetail.setContent(genres.getString("content"));
        }
        return reviewDetail;
    }

    public static List<ReviewDetails> parseReview(String result) {
        try {
            return parseReview(new JSONObject(result).getJSONArray("genres"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void parseMovieResult(String result, Movies.Result resultCallBack) {

        try {
            JSONObject obj = new JSONObject(result);
            if (obj.has("total_results")) {
                resultCallBack.setTotalResult(obj.getInt("total_results"));
            }
            if (obj.has("total_pages")) {
                resultCallBack.setTotalPages(obj.getInt("total_pages"));
            }
            if (obj.has("page")) {
                resultCallBack.setNextPage(obj.getInt("page"));
            }

            resultCallBack.setMovieList(ParseResult.parseMovieList(result));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
