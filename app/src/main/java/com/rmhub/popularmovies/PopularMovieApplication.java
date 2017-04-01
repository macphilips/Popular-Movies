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

   private boolean cacheEnable = false;

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
            int index = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.sort_key), "-1"));
            String[] titles = getResources().getStringArray(R.array.pref_sort_list_titles);
            if (index >= 0 && index < titles.length) {
                NetworkUtil.setDefaultEndpointUrl(titles[index]);
            }
        }

        if (key.equalsIgnoreCase(getString(R.string.cache_image))) {
            cacheEnable = sharedPreferences.getBoolean(getResources().getString(R.string.cache_image), false);
        }
    }

    public boolean isCacheEnable() {
        return cacheEnable;
    }
}
