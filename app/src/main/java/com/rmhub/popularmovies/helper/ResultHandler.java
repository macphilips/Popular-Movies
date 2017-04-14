package com.rmhub.popularmovies.helper;

import android.content.Context;

import com.rmhub.popularmovies.model.MovieDetail;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public abstract class ResultHandler {
    public abstract void onFetchResult(String result);

    public void saveToDatabase(Context context) {
    }
    public void saveToDatabase(Context context, MovieDetail moveID){}
}
