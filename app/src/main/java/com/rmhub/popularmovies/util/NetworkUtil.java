package com.rmhub.popularmovies.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.helper.MovieQuery;
import com.rmhub.popularmovies.helper.ResultHandler;
import com.rmhub.popularmovies.model.MovieDetail;
import com.rmhub.popularmovies.model.VideoDetail;

import java.util.Locale;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class NetworkUtil {
    private static final String BASE_URL = "https://api.themoviedb.org";
    private static final String API_VERSION = "/3";
    private static final String API_URL = BASE_URL + API_VERSION;
    private static final String MOVIE_ENDPOINT_URL = API_URL + "/movie";
    private static final String POPULAR_MOVIE_ENDPOINT_URL = MOVIE_ENDPOINT_URL + "/popular";
    private static final String TOP_RATED_MOVIES_ENDPOINT_URL = MOVIE_ENDPOINT_URL + "/top_rated";
    private final static String TAG = NetworkUtil.class.getSimpleName();
    private static String DEFAULT_ENDPOINT_URL = POPULAR_MOVIE_ENDPOINT_URL;
    private static NetworkUtil mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private NetworkUtil(Context context) {
        mCtx = context.getApplicationContext();
        Log.d(TAG, "is context the same with the application context? " + (context == context.getApplicationContext()));
        if (mCtx == null) {
            Log.d(TAG, "the application");
        }
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static String buildMoviesURL(int page_num) {
        return String.format(Locale.US,
                "%s?api_key=%s&page=%d&language=en-US", DEFAULT_ENDPOINT_URL, MovieDBApiKey.API_KEY, page_num);
    }

    public static void setDefaultEndpointUrl(Context ctx, String key) {
        if (key.equalsIgnoreCase(ctx.getString(R.string.sort_popular_value))) {
            DEFAULT_ENDPOINT_URL = POPULAR_MOVIE_ENDPOINT_URL;
        } else if (key.equalsIgnoreCase(ctx.getString(R.string.sort_top_rate_value))) {
            DEFAULT_ENDPOINT_URL = TOP_RATED_MOVIES_ENDPOINT_URL;
        }

    }

    public static String buildMovieReviewURL(MovieDetail details, int page_num) {
        return String.format(Locale.US,
                "%s/%d/reviews?api_key=%s&page=%d&language=en-US", MOVIE_ENDPOINT_URL, details.getMovieID(), MovieDBApiKey.API_KEY, page_num);

    }

    public static String buildMovieRecommendation(MovieDetail details, int page_num) {
        return String.format(Locale.US,
                "%s/%d/recommendations?api_key=%s&page=%d&language=en-US",
                MOVIE_ENDPOINT_URL, details.getMovieID(), MovieDBApiKey.API_KEY, page_num);

    }

    public static String buildMovieVideos(MovieDetail details, int page_num) {
        return String.format(Locale.US,
                "%s/%d/videos?api_key=%s&page=%d&language=en-US",
                MOVIE_ENDPOINT_URL, details.getMovieID(), MovieDBApiKey.API_KEY, page_num);

    }

    /**
     * Simple network connection check.
     *
     * @param context
     */
    private static boolean checkConnection(Context context) {
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

    public static String buildYoutubeVideoThumbnailURL(String videoID) {
        return String.format(Locale.US,
                "https://img.youtube.com/vi/%s/hqdefault.jpg", videoID);
    }

    public static synchronized NetworkUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkUtil(context);
        }
        return mInstance;
    }

    public static String buildYoutubeVideoURL(VideoDetail tag) {

        return String.format(Locale.US,
                "https://www.youtube.com/watch?v=%s", tag.getVideoID());
    }

    public <T extends ResultHandler> void fetchResult(MovieQuery query, Class<T> clazz, MovieRequest.MovieRequestListener<T> listener) {
        if (!checkConnection(mCtx)) {
            listener.onNetworkError();
            return;
        }

        MovieRequest<T> request = new MovieRequest<>(mCtx, query, clazz, listener);
        request.setTag(query.getClass().getSimpleName());
        addToRequestQueue(request);
    }

    public void cancelRequest(MovieQuery query) {
        mRequestQueue.cancelAll(query.getClass().getSimpleName());
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    private void addToRequestQueue(MovieRequest<? extends ResultHandler> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
