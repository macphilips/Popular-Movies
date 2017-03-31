package com.rmhub.popularmovies.helper;

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

public class ParserResult {
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
            movieDetails.setGenres(parseGenre(obj.getJSONArray("genres")));
        }
        if (obj.has("homepage")) {
            movieDetails.setHomepage(obj.getString("homepage"));
        }
        if (obj.has("id")) {
            movieDetails.setId(obj.getInt("id"));
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

    private static List<Genre> parseGenre(JSONArray genres)
            throws JSONException {
        List<Genre> genreList = new ArrayList<>();
        for (int i = 0, n = genres.length(); i < n; i++) {
            genreList.add(parseGenre(genres.getJSONObject(i)));
        }

        return genreList;
    }

    private static Genre parseGenre(JSONObject genres)
            throws JSONException {
        Genre genre = new Genre();
        if (genres.has("id")) {
            genre.setId(genres.getInt("id"));
        }
        if (genres.has("name")) {
            genre.setName(genres.getString("name"));
        }
        return genre;
    }

    public static List<Genre> parseGenre(String result) {
        try {
            return parseGenre(new JSONObject(result).getJSONArray("genres"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
