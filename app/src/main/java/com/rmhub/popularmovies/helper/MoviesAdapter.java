package com.rmhub.popularmovies.helper;

import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rmhub.popularmovies.PopularMovieApplication;
import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.utils.NetworkUtil;
import com.rmhub.popularmovies.utils.Utils;
import com.rmhub.simpleimagefetcher.ImageCache;
import com.rmhub.simpleimagefetcher.ImageFetcher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> implements LoadMoreCallback {
    private static final String CURRENT_PAGE_NUMBER = "nextPage";
    private static final String SCROLL_POSITION = "scroll_to";
    private static final String IMAGE_CACHE_DIR = "thumbnails";
    private final AppCompatActivity mContext;
    private final RecyclerView mRecyclerView;
    private final ImageFetcher mImageFetcher;
    private final List<MovieDetails> movieList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private Options options;
    private View.OnClickListener listener;
    private FrameLayout.LayoutParams mImageViewLayoutParams;
    private Loader mLoader;
    private ScrollChange mScrollChange;
    private int nextPage = 0, currentCount, totalCount, totalPages,
            numColumns, itemWidth, itemHeight;

    public MoviesAdapter(AppCompatActivity mContext, RecyclerView recyclerView, Options options) {
        setImageLayoutSize(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.mContext = mContext;
        this.options = options;
        this.mRecyclerView = recyclerView;
        mLayoutManager = recyclerView.getLayoutManager();
        mLoader = new Loader(mContext, options.loader_id);
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(mContext, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageFetcher = new ImageFetcher(mContext, mContext.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size));
        mImageFetcher.setLoadingImage(R.drawable.post_background);
        mImageFetcher.addImageCache(mContext.getSupportFragmentManager(), cacheParams);

        setUpRecyclerView();
    }

    public MoviesAdapter(AppCompatActivity mContext, RecyclerView recyclerView) {
        this(mContext, recyclerView, null);
    }

    private void setImageLayoutSize(int width, int height) {
        mImageViewLayoutParams = new FrameLayout.LayoutParams(width, height);
    }

    private void selectLoader(MovieDetails details, ImageView moviePoster) {
        Application app = mContext.getApplication();
        if (app instanceof PopularMovieApplication) {
            PopularMovieApplication popApp = (PopularMovieApplication) app;
            if (popApp.loader == 0) {
                mImageFetcher.loadImage(details.getPoster_path(), moviePoster);
            } else if (popApp.loader == 1) {
                Glide
                        .with(mContext)
                        .load(details.getPoster_path())
                        .centerCrop()
                        .placeholder(R.drawable.post_background)
                        .crossFade()
                        .into(moviePoster);
            } else if (popApp.loader == 2) {
                Picasso
                        .with(mContext)
                        .load(details.getPoster_path())
                        .centerCrop()
                        .placeholder(R.drawable.post_background)
                        .into(moviePoster);
            }

        } else {
            mImageFetcher.loadImage(details.getPoster_path(), moviePoster);
        }
    }

    private void setUpRecyclerView() {
        final int mImageThumbSpacing;
        final int mImageWidthSize;
        if (options.roughItemWidthSize > 0) {
            mImageWidthSize = options.roughItemWidthSize;
        } else {
            mImageWidthSize = mContext.getResources().getDimensionPixelSize(R.dimen.poster_column_size);
        }
        if (options.imagePadding > 0) {
            mImageThumbSpacing = options.imagePadding;
        } else {
            mImageThumbSpacing = 0;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mScrollChange = new ScrollChange(mLayoutManager, this);
        mRecyclerView.addOnScrollListener(mScrollChange);
        mRecyclerView.setAdapter(this);
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mLayoutManager.getWidth() / (mImageWidthSize + (mImageThumbSpacing * 2)));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mLayoutManager.getWidth() / numColumns) - mImageThumbSpacing;
                                setNumColumns(numColumns);

                                if (mLayoutManager instanceof GridLayoutManager) {
                                    ((GridLayoutManager) mLayoutManager).setSpanCount(numColumns);
                                }
                                mImageFetcher.setImageWidth(columnWidth);
                                setItemWidth(columnWidth);
                                if (Utils.hasJellyBean()) {
                                    mRecyclerView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mRecyclerView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });


    }

    public void reload() {
        movieList.clear();
        currentCount = 0;
        //    numColumns = 0;
        notifyDataSetChanged();
        refresh();
    }

    private void addMovieList(List<MovieDetails> movieList) {
        if (movieList == null) {
            return;
        }
        this.movieList.addAll(movieList);
        currentCount = this.movieList.size();
        notifyDataSetChanged();
        notifyItemChanged(currentCount);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MovieDetails tag = movieList.get(position);
        holder.container.setTag(tag);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.moviePoster.setTransitionName(String.format(Locale.US, "poster_%d", tag.getMovieID()));
        }

        holder.container.setOnClickListener(listener);
        holder.moviePoster.setLayoutParams(mImageViewLayoutParams);
        holder.moviePoster.setPadding(options.imagePadding, options.imagePadding, options.imagePadding, options.imagePadding);
        selectLoader(tag, holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return (movieList == null || numColumns == 0) ? 0 : movieList.size();
    }

    private boolean hasMoreItemToLoad() {
        return currentCount < totalCount;
    }

    private int getNumColumns() {
        return numColumns;
    }

    private void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public void setOnItemClickCallBack(View.OnClickListener listener) {
        this.listener = listener;
    }

    private void setItemWidth(int itemWidth) {
        if (this.itemWidth == itemWidth) {
            return;
        }
        this.itemWidth = itemWidth;
        this.itemHeight = (int) ((274 / 185f) * itemWidth);
        ;
        setImageLayoutSize(itemWidth, itemHeight);
        notifyDataSetChanged();
    }

    @Override
    public void loadMore() {
        if (hasMoreItemToLoad())
            loadMovie(nextPage);
    }

    private void loadMovie(int page_num) {
        Movies.Query query = new Movies.Query(NetworkUtil.buildMoviesURL(page_num));
        ddddd(query);
    }

    private void ddddd(Movies.Query query) {

        mLoader.addOnLoadCallBack(new Loader.MovieLoaderCallBack() {

            @Override
            public void onLoadComplete(ResultCallback callback) {
                if (callback instanceof Movies.Result) {
                    Movies.Result result = (Movies.Result) callback;

                    totalCount = result.getTotalCounts();
                    totalPages = result.getTotalPages();
                    nextPage = result.getNextPage();
                    addMovieList(result.getMovieList());
                    mScrollChange.setLoading();
                }
            }
        });
        mLoader.load(query);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_PAGE_NUMBER, nextPage);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        nextPage = savedInstanceState.getInt(CURRENT_PAGE_NUMBER, 1);
        // mRecyclerView.scrollToPosition(scrollTo);
    }

    public Loader getMovieLoader() {
        return mLoader;
    }

    public void refresh() {
        loadMovie(1);
    }

    public void refresh(Movies.Query query) {
        ddddd(query);
    }

    public static class Options {
        public int roughItemWidthSize = -1;
        public int imagePadding = -1;
        public int loader_id = 101;

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_container)
        View container;

        @BindView(R.id.movie_poster)
        ImageView moviePoster;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
