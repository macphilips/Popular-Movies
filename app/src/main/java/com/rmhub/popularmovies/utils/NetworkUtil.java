package com.rmhub.popularmovies.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.rmhub.popularmovies.helper.MovieDetails;
import com.rmhub.popularmovies.helper.ResultCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class NetworkUtil {
    public static final String BASE_URL = "https://api.themoviedb.org";
    private static final String API_VERSION = "/3";
    private static final String API_URL = BASE_URL + API_VERSION;
    private static final String MOVIE_ENDPOINT_URL = API_URL + "/movie";
    private static final String POPULAR_MOVIE_ENDPOINT_URL = MOVIE_ENDPOINT_URL + "/popular";
    private static final String TOP_RATED_MOVIES_ENDPOINT_URL = MOVIE_ENDPOINT_URL + "/top_rated";
    private static final String GENRE_LIST_ENDPOINT_URL = API_URL + "/genre/movie/list";
    public static String DEFAULT_ENDPOINT_URL = POPULAR_MOVIE_ENDPOINT_URL;


    public static void getPopularMovies(int page_num, ResultCallback callback) {
        String url = String.format(Locale.US,
                "%s?api_key=%s&page=%d", DEFAULT_ENDPOINT_URL, MovieDBApiKey.API_KEY, page_num);
        fetchResult(url, callback);
    }

    public static void setDefaultEndpointUrl(String key) {
        Log.d(NetworkUtil.class.getSimpleName(), "Setting default url endpoint");
        switch (key) {
            case "Popular Movies":
                DEFAULT_ENDPOINT_URL = POPULAR_MOVIE_ENDPOINT_URL;
                break;
            case "Top Rated Movies":
                DEFAULT_ENDPOINT_URL = TOP_RATED_MOVIES_ENDPOINT_URL;
                break;
        }
    }


    public static void getGenreMovies(int page_num, ResultCallback callback) {
        String url = String.format(Locale.US,
                "%s?api_key=%s&page=%d", GENRE_LIST_ENDPOINT_URL, MovieDBApiKey.API_KEY, page_num);
        fetchResult(url, callback);
    }

    public static void getMovieDetails(MovieDetails details, ResultCallback callback) {
        String url = String.format(Locale.US,
                "%s/%d?api_key=%s", MOVIE_ENDPOINT_URL, details.getId(), MovieDBApiKey.API_KEY);
        fetchResult(url, callback);
    }

    @Nullable
    private static void fetchResult(String url_string, ResultCallback callback) {
        URL url;
        String result = "";
        BufferedReader in;
        try {
            Log.d("sending post request", "url=" + url_string);
            url = new URL(url_string);
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.connect();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            String code = conn.getRequestMethod();
            while ((inputLine = in.readLine()) != null) {
                result = result.concat(inputLine);
            }
            callback.onFetchResult(result);
            Log.d("", String.valueOf(code));
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        } catch (NumberFormatException ignored) {
        }
        Log.d("sendPostHttpRequest", String.valueOf(result));
    }

}
