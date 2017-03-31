package com.rmhub.popularmovies.ui;

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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.helper.MovieDetails;
import com.rmhub.simpleimagefetcher.ImageCache;
import com.rmhub.simpleimagefetcher.ImageFetcher;
import com.rmhub.simpleimagefetcher.ImageWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MovieDetailsActivity extends AppCompatActivity implements ImageWorker.OnImageLoadedListener {

    public static final String MOVIES_DETAILS = "details";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    ImageCache.ImageCacheParams cacheParams = null;
    int mImageThumbSize;
    private GradientDrawable grad;
    private ShapeDrawable shapeDrawable;
    private Toolbar toolbar;

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

        setContentView(R.layout.activity_movie_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.poster_thumbnail_size_w92);
        cacheParams.setMemCacheSizePercent(0.25f);

        ImageFetcher imageFetcher = new ImageFetcher(this, getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size) * 2);
        imageFetcher.setLoadingImage(R.drawable.post_background);
        imageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        MovieDetails details = getIntent().getParcelableExtra(MOVIES_DETAILS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(R.id.movie_poster).setTransitionName(String.format(Locale.US, "poster_%d", details.getId()));
        }

        ((TextView) findViewById(R.id.movie_plot)).setText(String.valueOf(details.getOverview()));
        ((TextView) findViewById(R.id.movie_release_year)).setText(details.getRelease_date());
        ((TextView) findViewById(R.id.movie_title)).setText(String.valueOf(details.getTitle()));
        ((TextView) findViewById(R.id.movie_rating)).setText(String.format(Locale.US, "%.1f/%d", details.getVote_average(), 10));

        imageFetcher.loadImage(details.getBackdrop_path(), (ImageView) findViewById(R.id.backdrop_photo), this);

        imageFetcher.loadImage(details.getPoster_path(), (ImageView) findViewById(R.id.movie_poster));
        findViewById(R.id.movie_poster).getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        findViewById(R.id.movie_poster).getViewTreeObserver().removeOnPreDrawListener(this);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            postponeEnterTransition();
                        } else {
                            supportStartPostponedEnterTransition();
                        }
                        return true;
                    }
                }
        );

        toolbar.setBackgroundColor(details.getRgb());
        toolbar.setTitleTextColor(details.getTitleTextColor());

        LinearLayout container = ((LinearLayout) findViewById(R.id.overview_contain));
        int height = container.getHeight();
        int width = container.getWidth();

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
            container.setBackground(bg);
        } else {
            container.setBackgroundDrawable(bg);
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

    @Override
    public void onImageLoaded(Bitmap success, ImageView view) {
        if (success != null) {
            createPaletteAsync(success, (MovieDetails) view.getTag());
        }
    }

    public void createPaletteAsync(Bitmap bitmap, final MovieDetails details) {

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // Use generated instance
                LinearLayout container = ((LinearLayout) findViewById(R.id.overview_contain));

                Palette.Swatch vibrantSwatch = getDominantColor(p);
                if (vibrantSwatch != null) {
                    shapeDrawable.getPaint().setColor(vibrantSwatch.getRgb());

                    Drawable[] layers = {shapeDrawable, grad};
                    LayerDrawable bg = new LayerDrawable(layers);

                    toolbar.setBackgroundColor(vibrantSwatch.getRgb());
                    toolbar.setTitleTextColor(vibrantSwatch.getTitleTextColor());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        container.setBackground(bg);
                    } else {
                        container.setBackgroundDrawable(bg);
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

}
