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
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.rmhub.popularmovies.model.ReviewDetail;
import com.rmhub.popularmovies.model.Video;
import com.rmhub.popularmovies.model.VideoDetail;
import com.rmhub.popularmovies.util.MovieRequest;
import com.rmhub.popularmovies.util.NetworkUtil;
import com.rmhub.popularmovies.util.ProviderUtil;
import com.rmhub.popularmovies.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String MOVIES_DETAILS = "details";
    public static final String MOVIES_BUNDLE = "bundle";
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

    @BindView(R.id.recommendation_progress_bar)
    ProgressBar recommendation_progress_bar;
    @BindView(R.id.review_progress_bar)
    ProgressBar review_progress_bar;
    @BindView(R.id.empty_view_container)
    View emptyView;

    private boolean markedAsFav;
    private Button readMoreButton;
    private GradientDrawable grad;
    private ShapeDrawable shapeDrawable;
    private MovieDetail details;
    private TrailerVideoAdapter trailerVideoAdapter;
    private MoviesAdapter mAdapter;

    Single<Movies.Result> mRecommendationObservable = Single.fromCallable(new Callable<Movies.Result>() {
        @Override
        public Movies.Result call() {
            Movies.Result result = new Movies.Result();
            result.loadFromDB(MovieDetailsActivity.this,details);
            return result;
        }
    });

    private View.OnClickListener mItemClickCallback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    private Single<Review.Result> mReviewObservable = Single.fromCallable(new Callable<Review.Result>() {
        @Override
        public Review.Result call() {
            Review.Result result = new Review.Result();
            result.loadFromDB(MovieDetailsActivity.this, details);
            return result;
        }
    });
    private Single<Video.Result> mVideoObservable = Single.fromCallable(new Callable<Video.Result>() {
        @Override
        public Video.Result call() {
            Video.Result result = new Video.Result();
            result.loadFromDB(MovieDetailsActivity.this, details);
            return result;
        }
    });

    private Subscription mReviewSubscription = null;
    private Subscription mVideoSubscription = null;

    private Subscription mMarkedAsFavSubscription = null;

    private static SpannableStringBuilder addClickablePartTextViewResizable(final String str, final TextView tv,
                                                                            final String spannableText, final boolean viewMore) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);

        if (str.contains(spannableText)) {

            ssb.setSpan(new MySpanable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "View Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "View More", true);
                    }
                }
            }, str.indexOf(spannableText), str.indexOf(spannableText) + spannableText.length(), 0);

        }
        return ssb;

    }


    private Subscription mRecommendationSubscription = null;

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (Utils.hasJellyBean()) {
                    tv.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    tv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - 4 - expandText.length() + 1) + "...." + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable((tv.getText().toString()), tv, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable((tv.getText().toString()), tv, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable((tv.getText().toString()), tv, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private void hideReviewProgressBar() {
        review_progress_bar.setVisibility(View.GONE);
    }

    private void showReviewProgressBar() {
        review_progress_bar.setVisibility(View.VISIBLE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribeMovies();
        unSubscribeReview();
        unSubscribeVideos();
        unSubscribeMarkedAsFav();
    }

    private void showRecommendationProgressBar() {
        recommendation_progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideRecommendationProgressBar() {
        recommendation_progress_bar.setVisibility(View.GONE);
    }

    private void setupTrailerVideoAdapter(MovieDetail details) {

        trailerVideoAdapter = new TrailerVideoAdapter(details, this);
        Bundle bundle = new Bundle();
        bundle.putString(MovieRequest.QUERY_URL, NetworkUtil.buildMovieVideos(details, 1));
        bundle.putParcelable(MovieRequest.MOVIE_DETAILS, details);
        Video.Query videoQuery = new Video.Query(bundle);
        NetworkUtil.getInstance(this).fetchResult(videoQuery, Video.Result.class, new MovieRequest.MovieRequestListener<Video.Result>() {
            @Override
            public void onResponse(Video.Result result) {
                super.onResponse(result);
                trailerVideoAdapter.addVideoDetail(result.getVideoDetails());
            }

            @Override
            public void onNetworkError() {
                loadVideosFromDB();
            }
        });
        trailerVideoAdapter.setPlayListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtil.buildYoutubeVideoURL((VideoDetail) v.getTag()))));

            }
        });
        trailerVideoAdapter.setShareListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //     Object tag = v.getTag();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, NetworkUtil.buildYoutubeVideoURL((VideoDetail) v.getTag()));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            }
        });
        mPager.setAdapter(trailerVideoAdapter);
        indicatorView = (PageIndicatorView) findViewById(R.id.pageindicatorview);
        indicatorView.setViewPager(mPager);
        indicatorView.setOrientation(Orientation.HORIZONTAL);
    }

    private void setupReview(MovieDetail details) {
        Bundle bundle = new Bundle();
        bundle.putString(MovieRequest.QUERY_URL, NetworkUtil.buildMovieReviewURL(details, 1));
        bundle.putParcelable(MovieRequest.MOVIE_DETAILS, details);
        Review.Query reviewQuery = new Review.Query(bundle);
        NetworkUtil.getInstance(this).fetchResult(reviewQuery, Review.Result.class, new MovieRequest.MovieRequestListener<Review.Result>() {
            @Override
            public void onResponse(Review.Result response) {
                super.onResponse(response);
                hideReviewProgressBar();
                setupReviewLayout(response.getDetails());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                hideReviewProgressBar();
            }

            @Override
            public void onNetworkError() {
                loadReviewFromDB();
            }
        });
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

    public void setMarkAsFavBackground() {
        Drawable drawable;
        String text = null;
        int color;
        if (markedAsFav) {
            drawable = getResources().getDrawable(R.drawable.marked_as_favorite);
            text = "Marked as Favorite";
            color = Color.WHITE;
        } else {

            drawable = getResources().getDrawable(R.drawable.mark_as_favorite);
            text = "Mark as Favorite";
            color = Color.BLACK;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            markAsFavButton.setBackground(drawable);
        } else {
            markAsFavButton.setBackgroundDrawable(drawable);
        }
        markAsFavButton.setTextColor(color);
        markAsFavButton.setText(text);
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

    public void createPaletteAsync(Bitmap bitmap) {

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {                // Use generated instance
                Palette.Swatch vibrantSwatch = getDominantColor(p);
                if (vibrantSwatch != null) {
                    shapeDrawable.getPaint().setColor(vibrantSwatch.getRgb());
                    Drawable[] layers = {shapeDrawable, grad};
                    LayerDrawable bg = new LayerDrawable(layers);

                    toolbar.setTitleTextColor(vibrantSwatch.getTitleTextColor());
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

    @OnClick(R.id.mark_as_favorite)
    synchronized void markAsFavClicked() {
        if (details != null) {
            markedAsFav = !markedAsFav;
            Single<Boolean> favObserver = Single.fromCallable(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return ProviderUtil.updateFavorite(MovieDetailsActivity.this, details.getMovieID(), markedAsFav);
                }
            });
            favObserver
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleSubscriber<Boolean>() {
                        @Override
                        public void onSuccess(Boolean value) {
                            markedAsFav = value;
                            details.setFavorite(value);
                            setMarkAsFavBackground();
                        }

                        @Override
                        public void onError(Throwable error) {
                            ErrorDialog.show("Error occurred while loading data from database", getSupportFragmentManager());
                        }
                    });
        }
    }

    private void markedAsFavorite() {
        Single<Boolean> favObserver = Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return ProviderUtil.markedAsFavorite(MovieDetailsActivity.this, details.getMovieID());
            }
        });
        mMarkedAsFavSubscription = favObserver
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean value) {
                        markedAsFav = value;
                        details.setFavorite(value);
                        setMarkAsFavBackground();
                    }

                    @Override
                    public void onError(Throwable error) {
                        ErrorDialog.show("Error occurred while loading data from database", getSupportFragmentManager());
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        readMoreButton = new Button(this);

        details = getIntent().getBundleExtra(MOVIES_BUNDLE).getParcelable(MOVIES_DETAILS);
        markedAsFavorite();
        setupView(details);
        setupShapeDrawable();
        Glide
                .with(this)
                .load(details.getPosterURL())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .fitCenter()
                .placeholder(R.drawable.empty_photo).listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                           boolean isFromMemoryCache, boolean isFirstResource) {
                createPaletteAsync(resource);
                return false;
            }
        })
                .into(moviePoster);

        setupReview(details);


        setupRecommendationAdapter(details);


        setupTrailerVideoAdapter(details);
    }

    private void setupReviewLayout(final ArrayList<ReviewDetail> details) {
        if (!details.isEmpty()) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider));
            int n = Math.min(details.size(), 3);
            for (int i = 0; i < n; i++) {
                View v = getLayoutInflater().inflate(R.layout.review_item, null);
                v.setLayoutParams(params);
                params.setMargins(30, 10, 30, 10);
                ((TextView) v.findViewById(R.id.reviewer_name)).setText(details.get(i).getAuthor());
                TextView review = (TextView) v.findViewById(R.id.review);
                review.setText(details.get(i).getContent());
                makeTextViewResizable(review, 5, "more", true);
                String firstLetter = details.get(i).getAuthor().substring(0, 1).toUpperCase();
                TextDrawable drawable1 = TextDrawable.builder()
                        .beginConfig()
                        .bold()
                        // .withBorder(this.getResources().getDimensionPixelSize(R.dimen.review_avatar_spacing))
                        .endConfig()
                        .buildRoundRect(firstLetter, ColorGenerator.MATERIAL.getColor(details.get(i).getAuthor()), this.getResources().getDimensionPixelSize(R.dimen.review_avatar_size));

                View avatar = v.findViewById(R.id.review_avatar);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    avatar.setBackground(drawable1);
                } else {
                    avatar.setBackgroundDrawable(drawable1);
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
                readMoreButton.setTag(details);
                readMoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MovieDetailsActivity.this, ReviewScreen.class);
                        Bundle bundle = new Bundle();

                        bundle.putParcelableArrayList(ReviewScreen.REVIEW_LIST, details);

                        i.putExtra(ReviewScreen.REVIEW_LIST_BUNDLE, bundle);
                        startActivity(i);
                    }
                });
            } else {
                readMoreButton = null;
            }
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView v = new TextView(this);
            v.setText(getString(R.string.no_review));
            mRvReview.addView(v, params);
        }
    }

    private void loadVideosFromDB() {
        mVideoSubscription = mVideoObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Video.Result>() {
                    @Override
                    public void onSuccess(Video.Result value) {
                        trailerVideoAdapter.addVideoDetail(value.getVideoDetails());
                    }

                    @Override
                    public void onError(Throwable error) {
                        ErrorDialog.show("Error occurred while loading data from database", getSupportFragmentManager());
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
        opt.emptyView = emptyView;
        opt.radius = getResources().getDimensionPixelSize(R.dimen.cardview_default_radius);

        mRvRecommendation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mAdapter = new MoviesAdapter(this, mRvRecommendation, opt);
        mAdapter.setOnItemClickCallBack(mItemClickCallback);
        mAdapter.setLoadAdapter(new MoviesAdapter.OnLoadAdapter() {
            @Override
            public void onSuccess() {
                hideRecommendationProgressBar();
            }

            @Override
            public void onError(String message) { Log.d(TAG,String.valueOf(message));

                if (message.equalsIgnoreCase(getResources().getString(R.string.no_network_connection_toast))) {

                    Log.d(TAG,"Load recommendation from DB");
                    loadRecommendationsFromDB();

                } else {
                    hideRecommendationProgressBar();
                    ErrorDialog.show(message, getSupportFragmentManager());

                }
            }
        });
        mAdapter.loadMovie(recommendationQuery);
    }


    private void loadReviewFromDB() {
        mReviewSubscription = mReviewObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Review.Result>() {
                    @Override
                    public void onSuccess(Review.Result value) {
                        // mAdapter.loadOfflineData(value);
                        hideReviewProgressBar();
                        setupReviewLayout(value.getDetails());
                    }

                    @Override
                    public void onError(Throwable error) {
                        ErrorDialog.show("Error occurred while loading data from database", getSupportFragmentManager());
                    }
                });
    }

    private void loadRecommendationsFromDB() {
        mRecommendationSubscription = mRecommendationObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Movies.Result>() {
                    @Override
                    public void onSuccess(Movies.Result value) {
                        Log.d(TAG,"onSuccess called setRecommendation result");
                        hideRecommendationProgressBar();
                        mAdapter.loadOfflineData(value);
                    }

                    @Override
                    public void onError(Throwable error) {
                        hideRecommendationProgressBar();

                        ErrorDialog.show("Error occurred while loading data from database", getSupportFragmentManager());
                    }
                });

    }

    private void unSubscribeMovies() {
        if (mRecommendationSubscription != null && !mRecommendationSubscription.isUnsubscribed()) {
            mRecommendationSubscription.unsubscribe();
        }

    }
    private void unSubscribeVideos() {
        if (mVideoSubscription != null && !mVideoSubscription.isUnsubscribed()) {
            mVideoSubscription.unsubscribe();
        }
    }

    private void unSubscribeReview() {
        if (mReviewSubscription != null && !mReviewSubscription.isUnsubscribed()) {
            mReviewSubscription.unsubscribe();
        }
    }

    private void unSubscribeMarkedAsFav() {
        if (mMarkedAsFavSubscription != null && !mMarkedAsFavSubscription.isUnsubscribed()) {
            mMarkedAsFavSubscription.unsubscribe();
        }
    }
}
