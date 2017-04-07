package com.rmhub.popularmovies.helper;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.utils.NetworkUtil;
import com.rmhub.popularmovies.utils.Utils;
import com.rmhub.simpleimagefetcher.ImageCache;
import com.rmhub.simpleimagefetcher.ImageFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.MyViewHolder> implements LoadMoreCallback {
    private static final String CURRENT_PAGE_NUMBER = "nextPage";
    private static final String SCROLL_POSITION = "scroll_to";
    private static final String IMAGE_CACHE_DIR = "thumbnails";
    private static List<MovieDetails> movieList = null;
    private final AppCompatActivity mContext;
    private final RecyclerView mRecyclerView;
    private final ImageFetcher mImageFetcher;
    private int currentCount;
    private int totalCount;
    private int totalPages;
    private int numColumns;
    private View.OnClickListener listener;
    private int itemWidth;
    private LinearLayout.LayoutParams mImageViewLayoutParams;
    private MovieLoader mMovieLoader;
    private int nextPage = 0;
    private GridLayoutManager mLayoutManager;
    private ScrollChange mScrollChange;
    private int scrollTo = -1;
    private int itemHeight;

    public PopularMoviesAdapter(AppCompatActivity mContext, RecyclerView recyclerView) {
        this.mContext = mContext;
        movieList = new ArrayList<>();
        this.mRecyclerView = recyclerView;
        setUpRecyclerView();
        mImageViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mMovieLoader = new MovieLoader(mContext);
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(mContext, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageFetcher = new ImageFetcher(mContext, mContext.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size));
        mImageFetcher.setLoadingImage(R.drawable.post_background);
        mImageFetcher.addImageCache(mContext.getSupportFragmentManager(), cacheParams);
    }

    private void setUpRecyclerView() {
        final int mImageThumbSpacing;
        final int mImageThumbSize;
        mImageThumbSize = mContext.getResources().getDimensionPixelSize(R.dimen.poster_column_size);
        mImageThumbSpacing = mContext.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_space);

        mLayoutManager = new GridLayoutManager(mContext, 3);
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
                                    mLayoutManager.getWidth() / (mImageThumbSize + (mImageThumbSpacing * 2)));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mLayoutManager.getWidth() / numColumns);
                                setNumColumns(numColumns);
                                mLayoutManager.setSpanCount(numColumns);
                                mImageFetcher.setImageSize(columnWidth);
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

    public void addMovieList(List<MovieDetails> movieList) {
        if (movieList == null) {
            return;
        }
        int end = PopularMoviesAdapter.movieList.size();
        for (int i = 0, n = movieList.size(); i < n; i++) {
            movieList.get(i).setId(i + end + 1);
        }
        PopularMoviesAdapter.movieList.addAll(movieList);
        currentCount = PopularMoviesAdapter.movieList.size();
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
            holder.moviePoster.setTransitionName(String.format(Locale.US, "poster_%d", tag.getId()));
        }

        holder.container.setOnClickListener(listener);
        holder.moviePoster.setLayoutParams(mImageViewLayoutParams);
  /*      Glide
                .with(mContext)
                .load(tag.getBackdrop_path())
                .override(itemWidth,itemHeight)
                .centerCrop()
                .placeholder(R.drawable.post_background)
                .crossFade()
                .into(holder.moviePoster);

        Picasso.with(mContext)
                .load(tag.getBackdrop_path())
                .resize(itemWidth, itemHeight)
                .placeholder(R.drawable.post_background)
                .centerCrop()
                .into(holder.moviePoster);*/

          mImageFetcher.loadImage(tag.getPoster_path(), holder.moviePoster);
        //ImageView dummy = new ImageView(mContext);
        //dummy.setTag(movieList.get(position));
        //mImageFetcher.loadImage(movieList.get(position).getBackdrop_path(), dummy, this);


    }

    @Override
    public int getItemCount() {
        return (movieList == null || numColumns == 0) ? 0 : movieList.size();
    }

    boolean hasMoreItemToLoad() {
        return currentCount < totalCount;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public void setOnItemClickCallBack(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setItemWidth(int itemWidth) {
        if (this.itemWidth == itemWidth) {
            return;
        }
        this.itemWidth = itemWidth;
        this.itemHeight = (int) ((274 / 185f) * itemWidth);
        ;
        mImageViewLayoutParams =
                new LinearLayout.LayoutParams(itemWidth, itemHeight);
        notifyDataSetChanged();
    }

    @Override
    public void loadMore() {
        loadMovie(nextPage);
    }

    private void loadMovie(int page_num) {
        Movies.Query query = new Movies.Query(NetworkUtil.buildMoviesURL(page_num));

        mMovieLoader.addOnLoadCallBack(new MovieLoader.MovieLoaderCallBack() {
            @Override
            public void onLoadComplete(Movies.Result result) {
                totalCount = result.getTotalCounts();
                totalPages = result.getTotalPages();
                nextPage = result.getNextPage();
                addMovieList(result.getMovieList());
                mScrollChange.setLoading();
            }
        });
        mMovieLoader.load(query);
    }

    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(CURRENT_PAGE_NUMBER, nextPage);
        outState.putInt(SCROLL_POSITION, mLayoutManager.findFirstCompletelyVisibleItemPosition());
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        nextPage = savedInstanceState.getInt(CURRENT_PAGE_NUMBER, 1);
        scrollTo = savedInstanceState.getInt(SCROLL_POSITION, 0);
        // mRecyclerView.scrollToPosition(scrollTo);
    }

    public MovieLoader getMovieLoader() {
        return mMovieLoader;
    }

    public void setMovieLoader(MovieLoader mMovieLoader) {
        this.mMovieLoader = mMovieLoader;
    }

    public void refresh() {
        loadMovie(1);
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
