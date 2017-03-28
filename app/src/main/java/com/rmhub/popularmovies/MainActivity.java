package com.rmhub.popularmovies;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.rmhub.simpleimagefetcher.ImageCache;
import com.rmhub.simpleimagefetcher.ImageFetcher;

public class MainActivity extends AppCompatActivity implements LoadMoreCallback {
    private static final String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSIONS = 1;
    private PopularMoviesAdapter mAdapter;
    private ScrollChange mScrollChange;

    ImageCache.ImageCacheParams cacheParams = null;
    int mImageThumbSize;
    private static final String IMAGE_CACHE_DIR = "thumbs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
        // The welcome screen for this app (only one that automatically shows)
        ImageFetcher imageFetcher = new ImageFetcher(this, mImageThumbSize);
        imageFetcher.setLoadingImage(R.drawable.post_background);
        imageFetcher.addImageCache( getSupportFragmentManager(), cacheParams);
        mAdapter = new PopularMoviesAdapter(this,imageFetcher);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_movie_list);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        rv.setLayoutManager(mLayoutManager);
        mScrollChange = new ScrollChange(mLayoutManager, this);
        rv.addOnScrollListener(mScrollChange);
        rv.setAdapter(mAdapter);

        startTask(1);
    }

    @Override
    public void loadMore(final int page_num) {
        startTask(page_num);
    }



    void startTask(int page_num) {
        if (requestPermission())
            new LoadMovieTask().execute(page_num);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean success = false;
        if (grantResults.length == PERMISSIONS.length) {
            success = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    success = false;
                    break;
                }
            }
        }
        if (!success) {
            ErrorDialog
                    .show(getResources().getString(R.string.permission_request), getSupportFragmentManager());
        } else {
            startTask(1);
        }

    }

    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (hasPermissionGranted(permission)) return false;
        }
        return true;
    }

    private boolean hasPermissionGranted(String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean requestPermission() {
        if (!hasPermissionsGranted(PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(PERMISSIONS)) {
                    ConfirmationDialog.newInstance(PERMISSIONS).show(getSupportFragmentManager(), "");
                } else {
                    requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
                }
                return false;
            }
        }
        return true;
    }

    private class LoadMovieTask extends AsyncTask<Integer, Void, Movies> {
        @Override
        protected void onPostExecute(Movies movies) {
            super.onPostExecute(movies);
            mAdapter.addMovieList(movies.list);
            mScrollChange.setLoading();
        }

        @Override
        protected Movies doInBackground(Integer... params) {
            Movies movie = new Movies();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            NetworkUtil.getPopularMovies(params[0], movie);
            return movie;
        }
    }
}
