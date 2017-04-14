package com.rmhub.popularmovies.helper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.rmhub.popularmovies.model.MovieDetail;
import com.rmhub.popularmovies.util.NetworkUtil;

/**
 * Created by MOROLANI on 4/10/2017
 * <p>
 * owm
 * .
 */

public class MovieLoader extends AsyncTaskLoader<ResultHandler> {
    public static final String QUERY_URL = "url";
    public static final String RESULT_CALLBACK_NAME = "name";
    public static final String MOVIE_DETAILS = "movieDetails";
    private final Bundle args;
    private String TAG = getClass().getSimpleName();
    private ResultHandler mResult;

    public MovieLoader(Context context, Bundle args) {
        super(context);
        this.args = args;

    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();

    }

    @Override
    public void deliverResult(ResultHandler cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            return;
        }
        //  ResultHandler oldCursor = mResult;
        mResult = cursor;

        if (isStarted()) {
            super.deliverResult(mResult);
        }
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading");
        if (args == null) {
            Log.d(TAG, "onStartLoading");

            return;
        }

        if (mResult != null) {
            Log.d(TAG, "same Data deliver the result");
            deliverResult(mResult);
        }
        if (takeContentChanged() || mResult == null) {
            Log.d(TAG, "forceReload");
            forceLoad();
        }

    }

    @Override
    public ResultHandler loadInBackground() {
        if (!NetworkUtil.checkConnection(getContext())) {
            return null;
        }
              /*  List<MovieDetail> details = ProviderUtil.getMovies(mContext);
                Log.d(getClass().getSimpleName(), "MovieList size " +details.size()+" " + details.isEmpty() );
                if (details != null && !details.isEmpty()) {

                    ResultHandler movie = new ResultHandler();
                    movie.setStatus(NetworkStatus.SUCCESSFUL);
                    movie.setStatusDesc("Movie was obtained from local database");
                    movie.setMovieList(details);
                    return movie;
                } */

        String url = args.getString(QUERY_URL);
        String objtype = args.getString(RESULT_CALLBACK_NAME);
        MovieDetail detail = args.getParcelable(MOVIE_DETAILS);
        try {
            Object movie = Class.forName(objtype).newInstance();
            if (movie instanceof ResultHandler) {
                ResultHandler mResult = (ResultHandler) movie;
                NetworkUtil.fetchResult(url, mResult);
                if (detail != null)
                    (mResult).saveToDatabase(getContext(), detail);
                else
                    (mResult).saveToDatabase(getContext());

                return mResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ResultHandler movie = new ResultHandler();
        return null;
    }
}
