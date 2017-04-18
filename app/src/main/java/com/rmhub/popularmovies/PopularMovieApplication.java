package com.rmhub.popularmovies;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.rmhub.popularmovies.util.NetworkUtil;

import java.io.BufferedInputStream;
import java.io.IOException;

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
        if (NetworkUtil.API_KEY.equalsIgnoreCase("<<API_KEY>>") || TextUtils.isEmpty(NetworkUtil.API_KEY)) {
            AssetManager am = getAssets();
            try {
                BufferedInputStream bis = new BufferedInputStream(am.open("key.txt"));
                StringBuilder builder = new StringBuilder();
                int b;
                byte buffer[] = new byte[32];
                while ((b = bis.read(buffer)) != -1) {
                    builder.append(new String(buffer));
                }
                NetworkUtil.API_KEY = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(getString(R.string.sort_key))) {
            NetworkUtil.setDefaultEndpointUrl(this, sharedPreferences.getString(getResources().getString(R.string.sort_key), getString(R.string.sort_default_value)));
        }

    }

}
