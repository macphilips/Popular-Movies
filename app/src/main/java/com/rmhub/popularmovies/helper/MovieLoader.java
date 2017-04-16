package com.rmhub.popularmovies.helper;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.rmhub.popularmovies.model.Movies;
import com.rmhub.popularmovies.util.ProviderUtil;

/**
 * Created by MOROLANI on 4/10/2017
 * <p>
 * owm
 * .
 */

public class MovieLoader extends AsyncTaskLoader<ResultHandler> {
    private String TAG = getClass().getSimpleName();
    private ResultHandler mResult;

    public MovieLoader(Context context) {
        super(context);
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();
    }

    @Override
    public void deliverResult(ResultHandler data) {
        if (isReset()) {
            return;
        }
        //  ResultHandler oldCursor = mResult;
        mResult = data;

        if (isStarted()) {
            super.deliverResult(mResult);
        }
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG, "onStartLoading");

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
        Movies.Result result = new Movies.Result();
        result.setCurrentPage(-1);
        result.setMovieList(ProviderUtil.getMovies(getContext()));
        result.setTotalPages(0);
        result.setTotalResult(0);
        return result;
    }
}
