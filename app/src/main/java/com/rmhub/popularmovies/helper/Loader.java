package com.rmhub.popularmovies.helper;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.ui.ConfirmationDialog;
import com.rmhub.popularmovies.ui.ErrorDialog;
import com.rmhub.popularmovies.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOROLANI on 4/7/2017
 * <p>
 * owm
 * .
 */

public class Loader implements LoaderManager.LoaderCallbacks<ResultCallback> {
    private static final java.lang.String QUERY_ = "query";
    private static final String RESULT_CALLBACK_NAME = "name";
    private final AppCompatActivity mContext;
    private int MOVIE_LOADER;
    private List<MovieLoaderCallBack> callBacks = new ArrayList<>();

    public Loader(AppCompatActivity mContext, int id) {
        this.mContext = mContext;
        MOVIE_LOADER = id;
        mContext.getSupportLoaderManager().initLoader(id, null, this);
    }

    public Loader(AppCompatActivity mContext) {
        this(mContext, 101);
    }

    private void startLoader(Query query) {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(QUERY_, query.query());
        queryBundle.putString(RESULT_CALLBACK_NAME, query.resultCallBackName());
        LoaderManager loaderManager = mContext.getSupportLoaderManager();
        android.support.v4.content.Loader<ResultCallback> movieLoader = loaderManager.getLoader(MOVIE_LOADER);
        if (movieLoader == null) {
            loaderManager.initLoader(MOVIE_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER, queryBundle, this);
        }
    }

    private void start() {
        for (MovieLoaderCallBack callBack : callBacks) {
            callBack.onLoadStart();
        }
    }

    private void complete(ResultCallback result) {
        for (MovieLoaderCallBack callBack : callBacks) {
            callBack.onLoadComplete(result);
        }
    }

    private void error(ResultCallback result) {
        for (MovieLoaderCallBack callBack : callBacks) {
            callBack.onLoadError(result);
        }
    }

    public void addOnLoadCallBack(@NonNull MovieLoaderCallBack callBack) {
        callBacks.add(callBack);
    }

    public void load(final Query query) {

        Dexter.withActivity(mContext)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {

            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    startLoader(query);
                } else {
                    ErrorDialog
                            .show(mContext.getResources().getString(R.string.permission_request),
                                    mContext.getSupportFragmentManager());
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                String[] PERMISSIONS = new String[permissions.size()];
                int i = 0;
                for (PermissionRequest request : permissions) {
                    PERMISSIONS[i++] = request.getName();
                }
                ConfirmationDialog.newInstance(PERMISSIONS).show(mContext.getSupportFragmentManager(), "");

            }
        }).check();
    }

    @Override
    public android.support.v4.content.Loader<ResultCallback> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ResultCallback>(mContext) {

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                start();
                forceLoad();
            }

            @Override
            public ResultCallback loadInBackground() {
                if (!NetworkUtil.checkConnection(mContext)) {
                    return null;
                }
              /*  List<MovieDetails> details = ProviderUtil.getMovies(mContext);
                Log.d(getClass().getSimpleName(), "MovieList size " +details.size()+" " + details.isEmpty() );
                if (details != null && !details.isEmpty()) {

                    ResultCallback movie = new ResultCallback();
                    movie.setStatus(NetworkStatus.SUCCESSFUL);
                    movie.setStatusDesc("Movie was obtained from local database");
                    movie.setMovieList(details);
                    return movie;
                } */

                String url = args.getString(QUERY_);
                String objtype = args.getString(RESULT_CALLBACK_NAME);
                try {
                    Object movie = Class.forName(objtype).newInstance();
                    if (movie instanceof ResultCallback) {
                        NetworkUtil.fetchResult(url, (ResultCallback) movie);
                        return (ResultCallback) movie;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                // ResultCallback movie = new ResultCallback();
                return null;
            }


        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<ResultCallback> loader, final ResultCallback data) {
        if (data != null) {
            complete(data);
        } else {
            //  error(result);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ResultCallback> loader) {
    }

    private interface LoaderListener {
        void onLoadStart();

        void onLoadComplete(ResultCallback result);

        void onLoadError(ResultCallback result);
    }

    public static class MovieLoaderCallBack implements LoaderListener {

        @Override
        public void onLoadStart() {

        }

        @Override
        public void onLoadComplete(ResultCallback result) {

        }

        @Override
        public void onLoadError(ResultCallback result) {

        }
    }
}
