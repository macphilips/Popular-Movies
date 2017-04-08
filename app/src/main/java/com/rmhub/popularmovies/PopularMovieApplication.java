package com.rmhub.popularmovies;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.rmhub.popularmovies.utils.NetworkUtil;

/**
 * Created by MOROLANI on 3/30/2017
 * <p>
 * owm
 * .
 */

public class PopularMovieApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {

    public int loader;
    private boolean cacheEnable = false;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        this.onSharedPreferenceChanged(settings, getString(R.string.sort_key));
        this.onSharedPreferenceChanged(settings, getString(R.string.loader_key));
        settings.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(getString(R.string.sort_key))) {
            NetworkUtil.setDefaultEndpointUrl(sharedPreferences.getString(getResources().getString(R.string.sort_key), getString(R.string.sort_default_value)));
        }
        if (key.equalsIgnoreCase(getString(R.string.loader_key))) {
            String value = (sharedPreferences.getString(getResources().getString(R.string.loader_key), getString(R.string.loader_default_value)));
            loader = Integer.parseInt(value);
        }
        if (key.equalsIgnoreCase(getString(R.string.cache_image))) {
            cacheEnable = sharedPreferences.getBoolean(getResources().getString(R.string.cache_image), false);
        }
    }

    public boolean isCacheEnable() {
        return cacheEnable;
    }
}
