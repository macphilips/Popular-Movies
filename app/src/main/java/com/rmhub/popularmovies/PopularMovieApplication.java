package com.rmhub.popularmovies;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.rmhub.popularmovies.util.NetworkUtil;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.io.BufferedInputStream;
import java.io.IOException;

import static org.acra.ReportField.ANDROID_VERSION;
import static org.acra.ReportField.APP_VERSION_NAME;
import static org.acra.ReportField.BRAND;
import static org.acra.ReportField.DEVICE_FEATURES;
import static org.acra.ReportField.LOGCAT;
import static org.acra.ReportField.PHONE_MODEL;
import static org.acra.ReportField.PRODUCT;
import static org.acra.ReportField.SHARED_PREFERENCES;
import static org.acra.ReportField.STACK_TRACE;
import static org.acra.ReportField.USER_APP_START_DATE;
import static org.acra.ReportField.USER_CRASH_DATE;

/**
 * Created by MOROLANI on 3/30/2017
 * <p>
 * owm
 * .
 */
@ReportsCrashes(mailTo ="tmorolari@gmail.com",mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
          resDialogOkToast = R.string.crash_dialog_ok_toast, // optional. displays a Toast message when the user accepts to send a report.
        resDialogTheme = R.style.AppTheme_Dialog //optional. default is Theme.Dialog
        ,
        customReportContent = {APP_VERSION_NAME, PHONE_MODEL, BRAND, PRODUCT, ANDROID_VERSION, STACK_TRACE, USER_APP_START_DATE, USER_CRASH_DATE, LOGCAT, DEVICE_FEATURES, SHARED_PREFERENCES})

public class PopularMovieApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate() { ACRA.init(this);
            super.onCreate();
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
