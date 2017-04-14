package com.rmhub.popularmovies.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rd.Orientation;
import com.rd.PageIndicatorView;
import com.rmhub.popularmovies.CustomImageView;
import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.helper.MoviesAdapter;
import com.rmhub.popularmovies.helper.ReviewAdapter;
import com.rmhub.popularmovies.helper.TrailerVideoAdapter;
import com.rmhub.popularmovies.model.MovieDetail;
import com.rmhub.popularmovies.model.Movies;
import com.rmhub.popularmovies.model.Review;
import com.rmhub.popularmovies.model.ReviewDetails;
import com.rmhub.popularmovies.model.VideoDetail;
import com.rmhub.popularmovies.util.MovieRequest;
import com.rmhub.popularmovies.util.NetworkUtil;
import com.rmhub.popularmovies.util.ProviderUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String MOVIES_DETAILS = "details";
    private static final String IMAGE_CACHE_DIR = "thumbnails";
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

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

    @BindView(R.id.movie_poster)
    CustomImageView moviePoster;

    @BindView(R.id.overview_bg)
    View overviewBg;
    @BindView(R.id.rv_recommendation)
    RecyclerView mRvRecommendation;
    @BindView(R.id.rv_review)
    LinearLayout mRvReview;

    @BindView(R.id.review_title)
    View mReview;
    @BindView(R.id.viewPager)
    ViewPager mPager;
    @BindView((R.id.pageindicatorview))
    PageIndicatorView indicatorView;
    @BindView(R.id.mark_as_favorite)
    Button markAsFavButton;
    private boolean markedAsFav;
    private Button readMoreButton;

    private GradientDrawable grad;
    private ShapeDrawable shapeDrawable;
    private View.OnClickListener mItemClickCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    private MovieDetail details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readMoreButton = new Button(this);

        details = getIntent().getParcelableExtra(MOVIES_DETAILS);
        markedAsFav = details.getFavorite();
        setupView(details);
        setupShapeDrawable();
        int posterWidth = getResources().getDimensionPixelSize(R.dimen.poster_width_size);
        Glide
                .with(this)
                .load(details.getPoster_path())
                .asBitmap()
                //  .override(posterWidth, (int) (( 1.5) * posterWidth))
                .fitCenter()
                .placeholder(R.drawable.post_background).listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                           boolean isFromMemoryCache, boolean isFirstResource) {
                createPaletteAsync(resource, null);
                return false;
            }
        })
                .into(moviePoster);

        getReviews(details);


        setupRecommendationAdapter(details);


        setupTrailerVideoAdapter(details);
    }

    public void setMarkAsFavBackground() {
        if (markedAsFav) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                markAsFavButton.setBackground(getResources().getDrawable(R.drawable.marked_as_favorite));
                markAsFavButton.setTextColor(Color.WHITE);
            } else {
                markAsFavButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_as_favorite));
                markAsFavButton.setTextColor(Color.BLACK);
            }
        }

    }

    private void getReviews(MovieDetail details) {
        Bundle bundle = new Bundle();
        bundle.putString(MovieRequest.QUERY_URL, NetworkUtil.buildMovieReviewURL(details, 1));
        bundle.putParcelable(MovieRequest.MOVIE_DETAILS, details);
        Review.Query reviewQuery = new Review.Query(bundle);
        NetworkUtil.getInstance(this).fetchResult(reviewQuery, Review.Result.class, new MovieRequest.MovieRequestListener<Review.Result>() {
            @Override
            public void onResponse(Review.Result response) {
                super.onResponse(response);
                setupReviewLayout(response.getDetails());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }

            @Override
            public void onNetworkError() {
                super.onNetworkError();
            }
        });
    }

    private void setupRecommendationAdapter(MovieDetail details) {

        Bundle bundle = new Bundle();
        bundle.putString(MovieRequest.QUERY_URL, NetworkUtil.buildMovieRecommendation(details, 1));
        bundle.putParcelable(MovieRequest.MOVIE_DETAILS, details);
        Movies.Query recommendationQuery = new Movies.Query(bundle);

        MoviesAdapter.Options opt = new MoviesAdapter.Options();
        opt.roughItemWidthSize = getResources().getDimensionPixelSize(R.dimen.poster_width_size);
        opt.marginLeft = getResources().getDimensionPixelSize(R.dimen.margin);
        opt.radius = getResources().getDimensionPixelSize(R.dimen.cardview_default_radius);

        mRvRecommendation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        MoviesAdapter mAdapter = new MoviesAdapter(this, mRvRecommendation, opt);
        mAdapter.setOnItemClickCallBack(mItemClickCallback);
        mAdapter.loadMovie(recommendationQuery);
    }

    private void setupReviewLayout(List<ReviewDetails> details) {
        if (details.isEmpty()) {
            mReview.setVisibility(View.GONE);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider));
        int n = Math.min(details.size(), 3);
        for (int i = 0; i < n; i++) {
            View v = getLayoutInflater().inflate(R.layout.review_item, null);
            v.setLayoutParams(params);
            params.setMargins(30, 10, 30, 10);


            ((TextView) v.findViewById(R.id.reviewer_name)).setText(details.get(i).getAuthor());
            ((TextView) v.findViewById(R.id.review)).setText(details.get(i).getContent());
            String firstLetter = details.get(i).getAuthor().substring(0, 1).toUpperCase();

            TextDrawable drawable1 = TextDrawable.builder()
                    .beginConfig()
                    .bold()
                    .withBorder(this.getResources().getDimensionPixelSize(R.dimen.review_avatar_spacing))
                    .endConfig()
                    .buildRoundRect(firstLetter, ColorGenerator.MATERIAL.getColor(details.get(i).getAuthor()), this.getResources().getDimensionPixelSize(R.dimen.review_avatar_size));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                (v.findViewById(R.id.review_avatar)).setBackground(drawable1);
            } else {
                (v.findViewById(R.id.review_avatar)).setBackgroundDrawable(drawable1);
            }
            mRvReview.addView(v);
            if (i >= 0 && i < n - 1 && n != 1) {
                View divider = new View(this);
                divider.setLayoutParams(dividerParams);
                dividerParams.setMargins(30, 10, 30, 10);
                divider.setBackgroundColor(getResources().getColor(R.color.load_button_start));
                mRvReview.addView(divider);
            }
        }
        if (details.size() > n) {
            readMoreButton.setLayoutParams(params);
            params.setMargins(30, 10, 30, 10);
            mRvReview.addView(readMoreButton);
            readMoreButton.setText(String.format(Locale.US, "Read All Reviews %d", details.size()));
            ReviewAdapter.setBackgroundState(this, readMoreButton, 0);
        } else {
            readMoreButton = null;
        }
    }

    private void setupTrailerVideoAdapter(MovieDetail details) {
        TrailerVideoAdapter adapter = new TrailerVideoAdapter(details, this);
        adapter.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtil.buildYoutubeVideoURL((VideoDetail) v.getTag()))));

            }
        });
        mPager.setAdapter(adapter);
        indicatorView = (PageIndicatorView) findViewById(R.id.pageindicatorview);
        indicatorView.setViewPager(mPager);
        indicatorView.setOrientation(Orientation.HORIZONTAL);
    }

    private void setupShapeDrawable() {
        int height = overviewBg.getHeight();
        int width = overviewBg.getWidth();

        RectShape rect = new RectShape();
        rect.resize(width, height);

        shapeDrawable = new ShapeDrawable(rect);

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

    private void setupView(MovieDetail details) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            (moviePoster).setTransitionName(String.format(Locale.US, "poster_%d", details.getMovieID()));
        }
        (moviePlot).setText(String.valueOf(details.getOverview()));
        (movieReleaseYear).setText(details.getRelease_date());
        (movieTitle).setText(String.valueOf(details.getTitle()));
        (movieRating).setText(String.format(Locale.US, "%.1f/%d", details.getVote_average(), 10));


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


    @OnClick(R.id.mark_as_favorite)
    void markAsFavClicked() {
        if (details != null) {
            markedAsFav = !markedAsFav;
            ProviderUtil.updateFavorite(this, details.getMovieID(), markedAsFav);
            details.setFavorite(markedAsFav);
            setMarkAsFavBackground();
        }
    }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createPaletteAsync(Bitmap bitmap, final MovieDetail details) {

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {                // Use generated instance

                Log.d(TAG, "createPaletteAsync");
                Palette.Swatch vibrantSwatch = getDominantColor(p);
                if (vibrantSwatch != null) {
                    shapeDrawable.getPaint().setColor(vibrantSwatch.getRgb());
                    Drawable[] layers = {shapeDrawable, grad};
                    LayerDrawable bg = new LayerDrawable(layers);

                    toolbar.setTitleTextColor(vibrantSwatch.getTitleTextColor());
                    Log.d(TAG, "Vibrant Color intValue is" + vibrantSwatch.getRgb() + " hexValue = " + Integer.toHexString(vibrantSwatch.getRgb()));
                    ReviewAdapter.setBackgroundState(MovieDetailsActivity.this, readMoreButton, vibrantSwatch.getRgb());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        overviewBg.setBackground(bg);
                        toolbar.setBackground(bg);
                    } else {
                        toolbar.setBackgroundDrawable(bg);
                        overviewBg.setBackgroundDrawable(bg);
                    }
                }

            }
        });
    }
}
