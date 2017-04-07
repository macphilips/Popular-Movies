package com.rmhub.popularmovies.helper;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
import com.rmhub.popularmovies.utils.ProviderUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOROLANI on 4/7/2017
 * <p>
 * owm
 * .
 */

public class MovieLoader implements LoaderManager.LoaderCallbacks<Movies.Result> {
    private static final java.lang.String QUERY_ = "query";
    private static final int MOVIE_LOADER = 22;
    private final AppCompatActivity mContext;
    private List<MovieLoaderCallBack> callBacks = new ArrayList<>();

    public MovieLoader(AppCompatActivity mContext) {
        this.mContext = mContext;
        mContext.getSupportLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    private void startLoader(Movies.Query query) {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(QUERY_, query.query());
        LoaderManager loaderManager = mContext.getSupportLoaderManager();
        Loader<Movies.Result> movieLoader = loaderManager.getLoader(MOVIE_LOADER);
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

    private void complete(Movies.Result result) {
        for (MovieLoaderCallBack callBack : callBacks) {
            callBack.onLoadComplete(result);
        }
    }

    private void error(Movies.Result result) {
        for (MovieLoaderCallBack callBack : callBacks) {
            callBack.onLoadError(result);
        }
    }

    public void addOnLoadCallBack(@NonNull MovieLoaderCallBack callBack) {
        callBacks.add(callBack);
    }

    public void load(final Movies.Query query) {

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
    public Loader<Movies.Result> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Movies.Result>(mContext) {

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                start();
                forceLoad();
            }

            @Override
            public Movies.Result loadInBackground() {
                if (!NetworkUtil.checkConnection(mContext)) {
                    return null;
                }
              /*  List<MovieDetails> details = ProviderUtil.getMovies(mContext);
                Log.d(getClass().getSimpleName(), "MovieList size " +details.size()+" " + details.isEmpty() );
                if (details != null && !details.isEmpty()) {

                    Movies.Result movie = new Movies.Result();
                    movie.setStatus(NetworkStatus.SUCCESSFUL);
                    movie.setStatusDesc("Movie was obtained from local database");
                    movie.setMovieList(details);
                    return movie;
                } */

                String url = args.getString(QUERY_);
                Movies.Result movie = new Movies.Result();
                NetworkUtil.fetchResult(url, movie);
                ProviderUtil.insertMovies(mContext, movie.getMovieList());
                return movie;
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<Movies.Result> loader, final Movies.Result data) {
        if (data != null) {
            complete(data);
        } else {
            final Movies.Result result = new Movies.Result();
            result.setStatusDesc("No Internet Connection");
            result.setStatus(NetworkStatus.NETWORK_ERROR);
          //  error(result);
        }
    }

    @Override
    public void onLoaderReset(Loader<Movies.Result> loader) {
    }

    private interface LoaderListener {
        void onLoadStart();

        void onLoadComplete(Movies.Result result);

        void onLoadError(Movies.Result result);
    }

    public static class MovieLoaderCallBack implements LoaderListener {

        @Override
        public void onLoadStart() {

        }

        @Override
        public void onLoadComplete(Movies.Result result) {

        }

        @Override
        public void onLoadError(Movies.Result result) {

        }
    }
}
