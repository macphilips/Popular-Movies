package com.rmhub.popularmovies.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.helper.MovieDetails;
import com.rmhub.popularmovies.helper.MovieLoader;
import com.rmhub.popularmovies.helper.Movies;
import com.rmhub.popularmovies.helper.PopularMoviesAdapter;
import com.rmhub.popularmovies.utils.Utils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SETTINGS = 2;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progressBar)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.rv_movie_list)
    RecyclerView mRecyclerView;

    private PopularMoviesAdapter mAdapter;
    private boolean showingIndicator;


    @OnClick(R.id.refresh_button)
    void refresh() {
        hideReloadButton();
        mAdapter.refresh();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mAdapter = new PopularMoviesAdapter(this, mRecyclerView);
        mAdapter.setOnItemClickCallBack(this);
        showIndicator();

        mAdapter.getMovieLoader().addOnLoadCallBack(new MovieLoader.MovieLoaderCallBack() {
            @Override
            public void onLoadComplete(Movies.Result result) {
                hideIndicator();
            }

            @Override
            public void onLoadError(Movies.Result result) {
                hideIndicator();
                ErrorDialog.show(result.getStatusDesc(), getSupportFragmentManager());
                showReloadButton();
            }
        });
        mAdapter.refresh();
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
