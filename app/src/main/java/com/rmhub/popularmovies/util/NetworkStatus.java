package com.rmhub.popularmovies.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by MOROLANI on 4/7/2017
 * <p>
 * owm
 * .
 */

public class NetworkStatus {
    // Declare the constants
    public static final int SUCCESSFUL = 0;
    public static final int SERVER_ERROR = 1;
    public static final int NETWORK_ERROR = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SUCCESSFUL, SERVER_ERROR, NETWORK_ERROR})
    public @interface Status {
    }
}
