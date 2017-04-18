package com.rmhub.popularmovies;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.rmhub.popularmovies.util.NetworkUtil;

/**
 * Created by MOROLANI on 3/30/2017
 * <p>
 * owm
 * .
 */

public class PopularMovieApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        this.onSharedPreferenceChanged(settings, getString(R.string.sort_key));
        settings.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(getString(R.string.sort_key))) {
            NetworkUtil.setDefaultEndpointUrl(this, sharedPreferences.getString(getResources().getString(R.string.sort_key), getString(R.string.sort_default_value)));
        }

    }

}
