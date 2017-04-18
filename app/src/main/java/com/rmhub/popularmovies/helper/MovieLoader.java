package com.rmhub.popularmovies.helper;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.rmhub.popularmovies.model.MovieDetail;

/**
 * Created by MOROLANI on 4/10/2017
 * <p>
 * owm
 * .
 */

public class MovieLoader<T extends ResultHandler> extends AsyncTaskLoader<ResultHandler> {
    private String TAG = getClass().getSimpleName();
    private final Class<T> mClazz;
    private final MovieDetail detail;
    private ResultHandler mResult;


    public MovieLoader(Context context, Class<T> tClass, MovieDetail detail) {
        super(context);
        mClazz = tClass;
        this.detail = detail;
    }

    public MovieLoader(Context context, Class<T> tClass) {
        this(context, tClass, null);
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
        try {
            mResult = mClazz.newInstance();
            if (detail != null) {
                mResult.loadFromDB(getContext(),detail);
            } else {
                mResult.loadFromDB(getContext());
            }
            Log.d("",mResult.toString());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mResult;
    }
}
