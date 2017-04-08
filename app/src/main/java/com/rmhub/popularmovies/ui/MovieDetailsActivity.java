package com.rmhub.popularmovies.ui;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rmhub.popularmovies.PopularMovieApplication;
import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.helper.Loader;
import com.rmhub.popularmovies.helper.MovieDetails;
import com.rmhub.popularmovies.helper.Movies;
import com.rmhub.popularmovies.helper.MoviesAdapter;
import com.rmhub.popularmovies.helper.Review;
import com.rmhub.popularmovies.helper.Video;
import com.rmhub.popularmovies.utils.NetworkUtil;
import com.rmhub.simpleimagefetcher.ImageCache;
import com.rmhub.simpleimagefetcher.ImageFetcher;
import com.rmhub.simpleimagefetcher.ImageWorker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements ImageWorker.OnImageLoadedListener {

    public static final String MOVIES_DETAILS = "details";
    private static final String IMAGE_CACHE_DIR = "thumbnails";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.movie_plot)
    TextView moviePlot;
    @BindView(R.id.movie_release_year)
    TextView movieReleaseYear;
    @BindView(R.id.movie_title)
    TextView movieTitle;
    @BindView(R.id.movie_rating)
    TextView movieRating;
    @BindView(R.id.backdrop_photo)
    ImageView backdropPhoto;
    @BindView(R.id.movie_poster)
    ImageView moviePoster;
    @BindView(R.id.overview_bg)
    View overviewBg;
    @BindView(R.id.rv_recommendation)
    RecyclerView mRvRecommendation;
    @BindView(R.id.rv_review)
    RecyclerView mRvReview;

    private GradientDrawable grad;
    private ShapeDrawable shapeDrawable;
    private ImageFetcher mImageFetcher;
    private PopularMovieApplication popApp;
    private MoviesAdapter mAdapter;
    private View.OnClickListener mItemClickCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public Palette.Swatch getDominantColor(Palette bitmap) {
        List<Palette.Swatch> swatchesTemp = (bitmap).getSwatches();
        List<Palette.Swatch> swatches = new ArrayList<Palette.Swatch>(swatchesTemp);
        Collections.sort(swatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch swatch1, Palette.Swatch swatch2) {
                return swatch2.getPopulation() - swatch1.getPopulation();
            }
        });
        return swatches.size() > 0 ? swatches.get(0) : null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        if (getApplication() instanceof PopularMovieApplication) {
            popApp = (PopularMovieApplication) getApplication();
        }
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MovieDetails details = getIntent().getParcelableExtra(MOVIES_DETAILS);
        Movies.Query recommendationQuery = new Movies.Query(NetworkUtil.buildMovieRecommendation(details, 1));
        Video.Query videoQuery = new Video.Query(NetworkUtil.buildMovieVideos(details, 1));
        Review.Query reviewQuery = new Review.Query(NetworkUtil.buildMovieReviewURL(details, 1));

        Loader videoLoader = new Loader(this,300);
        videoLoader.addOnLoadCallBack(null);
        videoLoader.load(videoQuery);

        Loader reviewLoader = new Loader(this,300);
        reviewLoader.addOnLoadCallBack(null);
        reviewLoader.load(reviewQuery);


        MoviesAdapter.Options opt = new MoviesAdapter.Options();
        opt.roughItemWidthSize = getResources().getDimensionPixelSize(R.dimen.poster_width_size);
        opt.imagePadding = 2 * getResources().getDimensionPixelSize(R.dimen.image_thumbnail_space);
        opt.loader_id = 202;

        mRvRecommendation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new MoviesAdapter(this, mRvRecommendation, opt);
        mAdapter.setOnItemClickCallBack(mItemClickCallback);
        mAdapter.refresh(recommendationQuery);


        selectImageLoader(details);

        setUpView(details);
        setUpShapeDrawable();
    }

    private void setUpShapeDrawable() {
        int height = overviewBg.getHeight();
        int width = overviewBg.getWidth();

        RectShape rect = new RectShape();
        rect.resize(width, height);

        shapeDrawable = new ShapeDrawable(rect);
        // shapeDrawable.getPaint().setColor(details.getRgb());

        int[] colors = {getResources().getColor(R.color.overlay_start), getResources().getColor(R.color.overlay_end)};

        grad = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        grad.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        grad.setShape(GradientDrawable.RECTANGLE);
        grad.setSize(width, height);

        Drawable[] layers = {shapeDrawable, grad};
        LayerDrawable bg = new LayerDrawable(layers);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            overviewBg.setBackground(bg);
        } else {
            overviewBg.setBackgroundDrawable(bg);
        }
    }

    private void setUpView(MovieDetails details) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            (moviePoster).setTransitionName(String.format(Locale.US, "poster_%d", details.getMovieID()));
        }
        (moviePlot).setText(String.valueOf(details.getOverview()));
        (movieReleaseYear).setText(details.getRelease_date());
        (movieTitle).setText(String.valueOf(details.getTitle()));
        (movieRating).setText(String.format(Locale.US, "%.1f/%d", details.getVote_average(), 10));

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);

        mImageFetcher = new ImageFetcher(this, getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size));
        mImageFetcher.setLoadingImage(R.drawable.post_background);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

        (moviePoster).getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        moviePoster.getViewTreeObserver().removeOnPreDrawListener(this);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            postponeEnterTransition();
                        } else {
                            supportStartPostponedEnterTransition();
                        }
                        return true;
                    }
                }
        );
    }

    private void selectImageLoader(MovieDetails details) {
        Application app = getApplication();
        if (app instanceof PopularMovieApplication) {
            popApp = (PopularMovieApplication) app;
            if (popApp.loader == 0) {
                mImageFetcher.loadImage(details.getPoster_path(), moviePoster, this);
                mImageFetcher.loadImage(details.getBackdrop_path(), backdropPhoto);
            } else if (popApp.loader == 1) {
                Glide
                        .with(this)
                        .load(details.getBackdrop_path())
                        .centerCrop()
                        .placeholder(R.drawable.post_background)
                        .crossFade()
                        .into(backdropPhoto);
                Glide
                        .with(this)
                        .load(details.getPoster_path())
                        .centerCrop()

                        .placeholder(R.drawable.post_background)
                        .crossFade()
                        .into(moviePoster);
            } else if (popApp.loader == 2) {
                Picasso
                        .with(this)
                        .load(details.getBackdrop_path())
                        .centerCrop()
                        .placeholder(R.drawable.post_background)
                        .into(backdropPhoto);
                Picasso
                        .with(this)
                        .load(details.getPoster_path())
                        .centerCrop()
                        .placeholder(R.drawable.post_background)
                        .into(moviePoster);
            }

        } else {
            mImageFetcher.loadImage(details.getPoster_path(), moviePoster, this);
            mImageFetcher.loadImage(details.getBackdrop_path(), backdropPhoto);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void createPaletteAsync(Bitmap bitmap, final MovieDetails details) {

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // Use generated instance

                Palette.Swatch vibrantSwatch = getDominantColor(p);
                if (vibrantSwatch != null) {
                    shapeDrawable.getPaint().setColor(vibrantSwatch.getRgb());

                    Drawable[] layers = {shapeDrawable, grad};
                    LayerDrawable bg = new LayerDrawable(layers);

                    toolbar.setBackgroundColor(vibrantSwatch.getRgb());
                    toolbar.setTitleTextColor(vibrantSwatch.getTitleTextColor());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        overviewBg.setBackground(bg);
                    } else {
                        overviewBg.setBackgroundDrawable(bg);
                    }
                }

            }
        });
    }

    private Palette.Swatch checkVibrantSwatch(Palette p) {
        Palette.Swatch vibrant = p.getVibrantSwatch();
        if (vibrant != null) {
            return vibrant;
        }
        // Throw error
        return null;
    }

    @Override
    public void onImageLoaded(Bitmap success, ImageView view) {
        createPaletteAsync(success, null);
    }
}
