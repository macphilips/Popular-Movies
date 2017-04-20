package com.rmhub.popularmovies.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.helper.MoviesAdapter;
import com.rmhub.popularmovies.model.MovieDetail;
import com.rmhub.popularmovies.model.Movies;
import com.rmhub.popularmovies.util.Utils;

import java.util.Locale;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SETTINGS = 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER = 400;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progressBar)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.rv_movie_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view_container)
    View emptyView;

    private MoviesAdapter mAdapter;
    private boolean showingIndicator;

    void refresh() {
        mAdapter.loadMovie();
    }

    Single<Movies.Result> moviesObservable = Single.fromCallable(new Callable<Movies.Result>() {
        @Override
        public Movies.Result call() {
            Movies.Result result = new Movies.Result();
            result.loadFromDB(MainActivity.this);
            return result;
        }
    });
    private Subscription mMoviesSubscription = null;
    Single<Movies.Result> favoriteMoviesObservable = Single.fromCallable(new Callable<Movies.Result>() {
        @Override
        public Movies.Result call() {
            Movies.Result result = new Movies.Result();
            result.loadFavFromDB(MainActivity.this);
            return result;
        }
    });
    private Subscription mFavoriteMoviesSubscription = null;

  private SharedPreferences mPref  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        MoviesAdapter.Options opt = new MoviesAdapter.Options();
        opt.roughItemWidthSize = getResources().getDimensionPixelSize(R.dimen.poster_column_size);
        opt.emptyView = emptyView;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new MoviesAdapter(this, mRecyclerView, opt);
        mAdapter.setOnItemClickCallBack(this);
        mAdapter.setLoadAdapter(new MoviesAdapter.OnLoadAdapter() {
            @Override
            public void onSuccess() {
                hideIndicator();
            }

            @Override
            public void onError(String message) {
                if (message.equalsIgnoreCase(getResources().getString(R.string.no_network_connection_toast))) {
                    loadMoviesFromDB();
                    showSnackMessage(message);

                } else {
                    hideIndicator();
                    ErrorDialog.show(message, getSupportFragmentManager());

                }
            }
        });mPref = PreferenceManager.getDefaultSharedPreferences(this);
        showIndicator();
        if (savedInstanceState == null) {
            String key = mPref.getString(getResources().getString(R.string.sort_key), getString(R.string.sort_default_value));
            if (!key.equalsIgnoreCase(getResources().getString(R.string.sort_favorite_value))) {
                mAdapter.loadMovie();
            }
        }
    }

    private void loadMoviesFromDB() {
        mMoviesSubscription = moviesObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Movies.Result>() {
                    @Override
                    public void onSuccess(Movies.Result value) {
                        hideIndicator();
                        mAdapter.loadOfflineData(value);
                    }

                    @Override
                    public void onError(Throwable error) {
                        hideIndicator();
                        ErrorDialog.show("Error occurred while loading data from database", getSupportFragmentManager());
                    }
                });

    }

    private void unSubscribeMovies() {
        if (mMoviesSubscription != null && !mMoviesSubscription.isUnsubscribed()) {
            mMoviesSubscription.unsubscribe();
        }

    }

    private void loadFavoriteMoviesFromDB() {
        mFavoriteMoviesSubscription = favoriteMoviesObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Movies.Result>() {
                    @Override
                    public void onSuccess(Movies.Result value) {
                        hideIndicator();
                        mAdapter.loadFavoriteData(value);
                    }

                    @Override
                    public void onError(Throwable error) {
                        hideIndicator();
                        ErrorDialog.show("Error occurred while loading data from database", getSupportFragmentManager());
                    }
                });

    }

    private void unSubscribeFavoriteMovies() {
        if (mFavoriteMoviesSubscription != null && !mFavoriteMoviesSubscription.isUnsubscribed()) {
            mFavoriteMoviesSubscription.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribeFavoriteMovies();
        unSubscribeMovies();
    }

    private void showSnackMessage(String message) {
        Snackbar.make(mRecyclerView,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String key = mPref.getString(getResources().getString(R.string.sort_key), getString(R.string.sort_default_value));
        if (key.equalsIgnoreCase(getResources().getString(R.string.sort_favorite_value))) {
            loadFavoriteMoviesFromDB();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS) {
            if (resultCode == SettingsActivity.SETTINGS_CHANGED) {
                reload();
            }
        }
    }

    private void reload() {
        String key = mPref.getString(getResources().getString(R.string.sort_key), getString(R.string.sort_default_value));
        if (key.equalsIgnoreCase(getResources().getString(R.string.sort_favorite_value))) {
            loadFavoriteMoviesFromDB();
        } else {
            mAdapter.reload();
        }
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
            case R.id.action_sort:
                showLabelsPopup();
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
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDetailsActivity.MOVIES_DETAILS, (MovieDetail) tag);
            i.putExtra(MovieDetailsActivity.MOVIES_BUNDLE, bundle);
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

    private void showLabelsPopup() {
        View view = findViewById(R.id.action_sort);
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String value = "";
                switch (item.getItemId()) {
                    case R.id.sort_popular:
                        value = (getResources().getString(R.string.sort_popular_value));
                        break;

                    case R.id.sort_rating:
                        value = (getResources().getString(R.string.sort_top_rate_value));
                        break;
                    case R.id.sort_favorite:
                        value = (getResources().getString(R.string.sort_favorite_value));
                        break;
                    default:
                        return false;
                }
                saveChanges(value);
                reload();
                return true;

            }
        });
        popup.inflate(R.menu.sort_menu);

        String key = mPref.getString(getResources().getString(R.string.sort_key), getString(R.string.sort_default_value));
        int id;
        if (key.equalsIgnoreCase(getResources().getString(R.string.sort_top_rate_value))) {
            id = (R.id.sort_rating);
        } else if(key.equalsIgnoreCase(getResources().getString(R.string.sort_favorite_value))){
            id=R.id.sort_favorite;
        }
        else {
            id = (R.id.sort_popular);
        }
        MenuItem popupItem = popup.getMenu().findItem(id);
        popupItem.setCheckable(true);
        popupItem.setChecked(true);
        popup.show();
    }

    void saveChanges(String value) {
        SharedPreferences.Editor prefsEditor = mPref.edit();
        prefsEditor.putString(getResources().getString(R.string.sort_key), value);
        prefsEditor.apply();
    }
}
