package com.rmhub.popularmovies;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.rmhub.simpleimagefetcher.ImageCache;
import com.rmhub.simpleimagefetcher.ImageFetcher;
import com.rmhub.simpleimagefetcher.ImageWorker;

import java.util.Locale;

public class MovieDetailsActivity extends AppCompatActivity implements ImageWorker.OnImageLoadedListener {

    public static final String MOVIES_DETAILS = "details";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    ImageCache.ImageCacheParams cacheParams = null;
    int mImageThumbSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.poster_thumbnail_size_w92);
        cacheParams.setMemCacheSizePercent(0.25f);
        // Set memory cache to 25% of app memory
        // The welcome screen for this app (only one that automatically shows)

        ImageFetcher imageFetcher = new ImageFetcher(this, mImageThumbSize);
        imageFetcher.setLoadingImage(R.drawable.post_background);
        imageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        MovieDetails details = getIntent().getParcelableExtra(MOVIES_DETAILS);

        imageFetcher.loadImage(details.getPoster_path(), (ImageView) findViewById(R.id.movie_poster));
        imageFetcher.loadImage(details.getBackdrop_path(), (ImageView) findViewById(R.id.backdrop_photo),this);

        ((TextView) findViewById(R.id.movie_plot)).setText(String.valueOf(details.getOverview()));
        ((TextView) findViewById(R.id.movie_release_year)).setText(details.getRelease_date());
        ((TextView) findViewById(R.id.movie_title)).setText(String.valueOf(details.getTitle()));
        ((TextView) findViewById(R.id.movie_rating)).setText(String.format(Locale.US, "%f/%d", details.getVote_average(), 10));

    }

    @Override
    public void onImageLoaded(Bitmap success) {
        if (success != null) {
            createPaletteAsync(success);
        }
    }
    public void createPaletteAsync(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // Use generated instance
            }
        });
    }
}
