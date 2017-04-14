package com.rmhub.popularmovies.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.helper.MoviesAdapter;
import com.rmhub.popularmovies.model.MovieDetail;
import com.rmhub.popularmovies.util.Utils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SETTINGS = 2;
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progressBar)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.rv_movie_list)
    RecyclerView mRecyclerView;

    private MoviesAdapter mAdapter;
    private boolean showingIndicator;


    @OnClick(R.id.refresh_button)
    void refresh() {
        hideReloadButton();
        mAdapter.loadMovie();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        MoviesAdapter.Options opt = new MoviesAdapter.Options();
        opt.roughItemWidthSize = getResources().getDimensionPixelSize(R.dimen.poster_column_size);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new MoviesAdapter(this, mRecyclerView, opt);
        mAdapter.setOnItemClickCallBack(this);
        mAdapter.setLoadAdapter(new MoviesAdapter.OnLoadAdapter() {
            @Override
            public void success() {
                hideIndicator();
            }

            @Override
            public void failed(String message) {
                hideIndicator();
                ErrorDialog.show(message, getSupportFragmentManager());
            //    showReloadButton();
            }
        });
        showIndicator();
        mAdapter.loadMovie();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v(TAG, "onResume Called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    private void showIndicator() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        showingIndicator = true;
    }

    private void hideIndicator() {
        if (showingIndicator) {
            mLoadingIndicator.setVisibility(View.GONE);
            showingIndicator = false;
        }
    }

    private void showReloadButton() {
        findViewById(R.id.error_container).setVisibility(View.VISIBLE);
        //  showingIndicator = true;
    }

    private void hideReloadButton() {
        findViewById(R.id.error_container).setVisibility(View.GONE);
        ///  showingIndicator = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS) {
            if (resultCode == SettingsActivity.SETTINGS_CHANGED) {
                reload();
            }
        }
    }

    private void reload() {
        mAdapter.reload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof MovieDetail) {
            Intent i = new Intent(this, MovieDetailsActivity.class);
            i.putExtra(MovieDetailsActivity.MOVIES_DETAILS, (MovieDetail) tag);
            if (Utils.hasJellyBean()) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, v,
                                String.format(Locale.US, "poster_%d", ((MovieDetail) tag).getMovieID()));
                startActivity(i, options.toBundle());
            } else {
                startActivity(i);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAdapter.onRestoreInstanceState(savedInstanceState);
    }

}
