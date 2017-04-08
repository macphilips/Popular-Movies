package com.rmhub.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private static final String GENRE_LIST_ENDPOINT_URL = API_URL + "/genre/movie/movieList";
    public static String DEFAULT_ENDPOINT_URL = POPULAR_MOVIE_ENDPOINT_URL;
    public static int movie_category = 0;


    public static String buildMoviesURL(int page_num) {
        return String.format(Locale.US,
                "%s?api_key=%s&page=%d&language=en-US", DEFAULT_ENDPOINT_URL, MovieDBApiKey.API_KEY, page_num);
    }

    public static void setDefaultEndpointUrl(String key) {
        switch (key) {
            case "popular":
                DEFAULT_ENDPOINT_URL = POPULAR_MOVIE_ENDPOINT_URL;
                movie_category = 1;
                break;
            case "top_rated":
                DEFAULT_ENDPOINT_URL = TOP_RATED_MOVIES_ENDPOINT_URL;
                movie_category = 2;
                break;
        }
    }


    public static String buildMovieReviewURL(MovieDetails details, int page_num) {
        return String.format(Locale.US,
                "%s/%d/reviews?api_key=%s&page=%d&language=en-US", MOVIE_ENDPOINT_URL, details.getMovieID(), MovieDBApiKey.API_KEY, page_num);

    }

    public static String buildMovieRecommendation(MovieDetails details, int page_num) {
        return String.format(Locale.US,
                "%s/%d/recommendations?api_key=%s&page=%d&language=en-US",
                MOVIE_ENDPOINT_URL, details.getMovieID(), MovieDBApiKey.API_KEY, page_num);

    }

    public static String buildMovieVideos(MovieDetails details, int page_num) {
        return String.format(Locale.US,
                "%s/%d/videos?api_key=%s&page=%d&language=en-US",
                MOVIE_ENDPOINT_URL, details.getMovieID(), MovieDBApiKey.API_KEY, page_num);

    }

    public static void getMovieDetails(MovieDetails details, ResultCallback callback) {
        String url = String.format(Locale.US,
                "%s/%d?api_key=%s", MOVIE_ENDPOINT_URL, details.getMovieID(), MovieDBApiKey.API_KEY);
        fetchResult(url, callback);
    }

    @Nullable
    public static void fetchResult(String url_string, ResultCallback callback) {
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

    /**
     * Simple network connection check.
     *
     * @param context
     */
    public static boolean checkConnection(Context context) {
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            //   Toast.makeText(context, com.rmhub.simpleimagefetcher.R.string.no_network_connection_toast, Toast.LENGTH_LONG).show();

            Log.e(NetworkUtil.class.getSimpleName(), "checkConnection - no connection found");
            return false;
        }
        return true;
    }

}
