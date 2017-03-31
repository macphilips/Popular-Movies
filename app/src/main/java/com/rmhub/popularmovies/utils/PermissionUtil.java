package com.rmhub.popularmovies.utils;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.rmhub.popularmovies.ui.ConfirmationDialog;

/**
 * Created by MOROLANI on 3/7/2017
 * <p>
 * owm
 * .
 */

public class PermissionUtil {
    public static final int REQUEST_PERMISSIONS = 1;
    private static final String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final String FRAGMENT_DIALOG = "Confirmation";
    private static String[] permissions = PERMISSIONS;

    private static boolean hasPermissionsGranted(Context mContext) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(mContext, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static boolean shouldShowRequestPermissionRationale(Fragment mFragment) {
        for (String permission : permissions) {
            if (mFragment.shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }
        return false;
    }

    public static boolean requestPermissions(String[] permissions, Fragment mFragment) {
        PermissionUtil.permissions = permissions;
        return request(mFragment);
    }

    public static boolean requestPermissions(Fragment mFragment) {
        PermissionUtil.permissions = PERMISSIONS;
        return request(mFragment);
    }

    private static boolean request(Fragment mFragment) {
        boolean request = hasPermissionsGranted(mFragment.getActivity());
        if (!request) {
            if (shouldShowRequestPermissionRationale(mFragment)) {
                ConfirmationDialog.newInstance(PermissionUtil.permissions).show(mFragment.getFragmentManager(), FRAGMENT_DIALOG);
            } else {
                mFragment.requestPermissions(PermissionUtil.permissions, REQUEST_PERMISSIONS);
            }
        }
        return request;
    }
}
