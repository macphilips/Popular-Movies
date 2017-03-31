package com.rmhub.popularmovies;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.rmhub.simpleimagefetcher.ImageCache;
import com.rmhub.simpleimagefetcher.ImageFetcher;
import com.rmhub.simpleimagefetcher.Utils;

import java.util.Locale;

import static com.rmhub.popularmovies.SettingsActivity.SETTINGS_CHANGED;

public class MainActivity extends AppCompatActivity implements LoadMoreCallback, View.OnClickListener {
    private static final String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSIONS = 1;
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private static final int SETTINGS = 2;
    ImageCache.ImageCacheParams cacheParams = null;
    private int mImageThumbSize;
    private PopularMoviesAdapter mAdapter;
    private ScrollChange mScrollChange;
    private ImageFetcher mImageFetcher;
    private int mImageThumbSpacing;
    private String loading = "Popular Movie";
    private boolean showdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.poster_column_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_space);
        cacheParams.setMemCacheSizePercent(0.25f);

        setupLayout();
        startTask(1);


    }

    private void setupLayout() {
        mImageFetcher = new ImageFetcher(this, getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size));
        mImageFetcher.setLoadingImage(R.drawable.post_background);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mAdapter = new PopularMoviesAdapter(this, mImageFetcher);
        mAdapter.setOnItemClickCallBack(this);
        final RecyclerView rv = (RecyclerView) findViewById(R.id.rv_movie_list);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        rv.setLayoutManager(mLayoutManager);
        mScrollChange = new ScrollChange(mLayoutManager, this);
        rv.addOnScrollListener(mScrollChange);
        rv.setAdapter(mAdapter);
        rv.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mLayoutManager.getWidth() / (mImageThumbSize + (mImageThumbSpacing * 2)));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mLayoutManager.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mLayoutManager.setSpanCount(numColumns);

                                //      mAdapter.setItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    printLog("onCreateView - numColumns set to %d", numColumns);
                                }
                                if (Utils.hasJellyBean()) {
                                    rv.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    rv.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.i(getClass().getSimpleName(), String.format(Locale.US, "RecyclerView.Height = %d, RecyclerView.width = %d",
                mLayoutManager.getHeight(), mLayoutManager.getWidth()));
        Log.i(getClass().getSimpleName(), String.format(Locale.US, "Height = %d, width = %d",
                height, width));
    }

    @Override
    public void loadMore(final int page_num) {
        startTask(page_num);
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.clearCache();

    }

    void startTask(int page_num) {
        if (requestPermission())
            new LoadMovieTask().execute(page_num);
    }

    private void printLog(String msg, Object... params) {
        Log.i(getClass().getSimpleName(), String.format(Locale.US, msg, params));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean success = false;
        if (requestCode == REQUEST_PERMISSIONS) {
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS) {
            if (resultCode == SETTINGS_CHANGED) {
                reload();
            }
        }
    }

    private void reload() {
        mAdapter.clearAdapter();
        mScrollChange.reset();
        startTask(1);
    }

    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (hasPermissionGranted(permission)) return false;
        }
        return true;
    }

    private boolean hasPermissionGranted(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED;
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

    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage(String.format("Loading %s from %s", loading, NetworkUtil.BASE_URL));
        showdialog = true;
        return dialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS);
                return true;
            case R.id.action_clear_cache:
                if (mImageFetcher != null) {
                    mImageFetcher.clearCache();
                    mAdapter.notifyDataSetChanged();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof MovieDetails) {
            Intent i = new Intent(this, MovieDetailsActivity.class);
            i.putExtra(MovieDetailsActivity.MOVIES_DETAILS, (MovieDetails) tag);
            if (Utils.hasJellyBean()) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, v,
                                String.format(Locale.US, "poster_%d", ((MovieDetails) tag).getId()));
                startActivity(i, options.toBundle());
            } else {
                startActivity(i);
            }
        }
    }

    private class LoadMovieTask extends AsyncTask<Integer, Void, Movies> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


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
