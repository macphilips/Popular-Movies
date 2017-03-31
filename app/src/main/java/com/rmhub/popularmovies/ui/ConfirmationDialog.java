package com.rmhub.popularmovies.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.utils.PermissionUtil;

/**
 * Created by MOROLANI on 2/16/2017
 * <p>
 * owm
 * .
 */
public class ConfirmationDialog extends DialogFragment {
    private static final String PERMISSIONS = "permissions";

    @SuppressLint("ValidFragment")
    private ConfirmationDialog(){}

    public static ConfirmationDialog newInstance(String[] permissions) {

        Bundle args = new Bundle();
        args.putCharSequenceArray(PERMISSIONS, permissions);
        ConfirmationDialog fragment = new ConfirmationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Fragment parent = getParentFragment();
        final String[] permissions = (String[]) getArguments().getCharSequenceArray(PERMISSIONS);
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.permission_request)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtil.requestPermissions(permissions, parent);
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                parent.getActivity().finish();
                            }
                        })
                .create();
    }

}