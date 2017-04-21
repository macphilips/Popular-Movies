package com.rmhub.popularmovies.helper;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rmhub.popularmovies.CustomImageView;
import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.model.MovieDetail;
import com.rmhub.popularmovies.model.Movies;
import com.rmhub.popularmovies.util.MovieRequest;
import com.rmhub.popularmovies.util.NetworkUtil;
import com.rmhub.popularmovies.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> implements LoadMoreCallback {
    private static final String CURRENT_PAGE_NUMBER = "nextPage";
    private static final String SCROLL_POSITION = "scroll_to";
    private static final String TAG = MoviesAdapter.class.getSimpleName();
    private static final String MOVIE_LIST = "movie_list";
    private static final String TOTAL_COUNT = "total_count";
    private static final String TOTAL_PAGE = "total_page";
    private static final String LIST_BUNDLE = "bundle";
    private final AppCompatActivity mContext;
    private final RecyclerView mRecyclerView;

    private ArrayList<MovieDetail> movieList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private Options options;
    private View.OnClickListener listener;
    private LinearLayout.LayoutParams mImageViewLayoutParams;
    private ScrollChange mScrollChange;
    private int nextPage = 1, currentCount, totalCount, totalPages,
            numColumns, itemWidth, itemHeight;
    private OnLoadAdapter onLoadAdapter;

    public MoviesAdapter(AppCompatActivity mContext, RecyclerView recyclerView, Options options) {
        setImageLayoutSize(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mContext = mContext;
        this.options = options;
        this.mRecyclerView = recyclerView;
        mLayoutManager = recyclerView.getLayoutManager();
        setUpRecyclerView();
    }

    public boolean isEmpty() {
        return movieList.isEmpty();
    }

    private void setUpRecyclerView() {
        final int mImageWidthSize;
        if (options.roughItemWidthSize > 0) {
            mImageWidthSize = options.roughItemWidthSize;
        } else {
            mImageWidthSize = mContext.getResources().getDimensionPixelSize(R.dimen.poster_column_size);
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
                                    mLayoutManager.getWidth() / (mImageWidthSize + (options.padding * 2)));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mLayoutManager.getWidth() / numColumns) - options.padding;
                                setNumColumns(numColumns);

                                if (mLayoutManager instanceof GridLayoutManager) {
                                    ((GridLayoutManager) mLayoutManager).setSpanCount(numColumns);
                                }
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

    private void setImageLayoutSize(int width, int height) {
        mImageViewLayoutParams = new LinearLayout.LayoutParams(width, height);
    }

    private void showEmptyView() {
        if (options.emptyView != null) {
            options.emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void hideEmptyView() {
        if (options.emptyView != null) {
            options.emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MovieDetail tag = movieList.get(position);
        holder.container.setTag(tag);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.moviePoster.setTransitionName(String.format(Locale.US, "poster_%d", tag.getMovieID()));
        }
        holder.container.setOnClickListener(listener);
        holder.moviePoster.setLayoutParams(mImageViewLayoutParams);
        holder.moviePoster.setRadius(options.radius);
        mImageViewLayoutParams.setMargins(options.marginLeft, options.marginTop, options.marginRight, options.marginBottom);

        Glide
                .with(mContext)
                .load(tag.getPosterURL())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(itemWidth, itemHeight)
                .fitCenter()
                .placeholder(R.drawable.empty_photo)
                .error(R.drawable.no_image)
                .crossFade()
                .into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return (movieList == null || numColumns == 0) ? 0 : movieList.size();
    }

    private boolean hasMoreItemToLoad() {
        return (currentCount < totalCount || nextPage < totalPages) && nextPage > 0;
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
        this.itemHeight = (int) (1.5 * itemWidth);
        ;
        setImageLayoutSize(itemWidth, itemHeight);
        notifyDataSetChanged();
    }

    public void onSaveInstanceState(Bundle outState) {

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(MOVIE_LIST, movieList);
        bundle.putInt(CURRENT_PAGE_NUMBER, nextPage);
        bundle.putInt(TOTAL_PAGE, totalPages);
        bundle.putInt(TOTAL_COUNT, totalCount);
        int position;
        if (mLayoutManager instanceof GridLayoutManager) {
            position = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        } else {
            position = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }
        bundle.putInt(SCROLL_POSITION, position);
        outState.putBundle(LIST_BUNDLE, bundle);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Bundle bundle = savedInstanceState.getBundle(LIST_BUNDLE);
        if (bundle != null) {
            ArrayList<MovieDetail> movieList = bundle.getParcelableArrayList(MOVIE_LIST);

            if (movieList != null) {
                resetAdapter();
                nextPage = bundle.getInt(CURRENT_PAGE_NUMBER);
                totalPages = bundle.getInt(TOTAL_PAGE);
                totalCount = bundle.getInt(TOTAL_COUNT);
                int scrollTo = bundle.getInt(SCROLL_POSITION);

                Log.d(TAG, "scrollto => "+scrollTo);
                addMovieList(movieList);
                mScrollChange.setLoading();
                mRecyclerView.smoothScrollToPosition(scrollTo);
                if (onLoadAdapter != null) {
                    onLoadAdapter.onSuccess();
                }
            }
        }
    }

    public void reload() {
        resetAdapter();
        loadMovie();
    }

    private void resetAdapter() {
        currentCount = 0;
        totalCount = 0;
        totalPages = 0;
        nextPage = 1;
        if (!movieList.isEmpty()) {
            movieList.clear();
            notifyDataSetChanged();
        }
    }

    private void addMovieList(List<MovieDetail> movieList) {
        if (movieList == null) {
            return;
        }
        if (this.movieList.isEmpty() && movieList.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
        this.movieList.addAll(movieList);
        currentCount = this.movieList.size();
        notifyDataSetChanged();
        notifyItemChanged(currentCount);
    }

    public void loadOfflineData(Movies.Result result) {
        if (nextPage != -1 && !movieList.isEmpty()) {
            return;
        }
        resetAdapter();
        setResult(result);
    }

    public void loadFavoriteData(Movies.Result result) {
        resetAdapter();
        setResult(result);
    }

    public void loadMovie() {
        loadMovie(nextPage);
    }

    private void loadMovie(int page_num) {
        Bundle bundle = new Bundle();
        bundle.putString(MovieRequest.QUERY_URL, NetworkUtil.buildMoviesURL(page_num));
        Movies.Query query = new Movies.Query(bundle);
        loadMovie(query);
    }

    public void loadMovie(Movies.Query query) {
        NetworkUtil.getInstance(mContext).fetchResult(query, Movies.Result.class,
                new MovieRequest.MovieRequestListener<Movies.Result>() {
            @Override
            public void onResponse(Movies.Result result) {
                super.onResponse(result);

                if (result.getCurrentPage() == 1) resetAdapter();
                setResult(result);
                if (onLoadAdapter != null) {
                    onLoadAdapter.onSuccess();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                mScrollChange.setLoading();
                if (onLoadAdapter != null) {
                    onLoadAdapter.onError("Unable to fetch data");
                }
                addMovieList(new ArrayList<MovieDetail>());
            }

            @Override
            public void onNetworkError() {
                super.onNetworkError();
                mScrollChange.setLoading();
                Log.d(TAG,"onNetworkError Called");
                if (onLoadAdapter != null) {
                    onLoadAdapter.onError(mContext.getResources().getString(R.string.no_network_connection_toast));
                    Log.d(TAG,"onloadadapter is not null");
                }
            }
        });
    }

    private void setResult(Movies.Result result) {
        totalCount = result.getTotalCounts();
        totalPages = result.getTotalPages();
        if (result.getCurrentPage() != -1)
            nextPage = result.getCurrentPage() + 1;
        else
            nextPage = result.getCurrentPage();

        addMovieList(result.getMovieList());
        mScrollChange.setLoading();
    }

    @Override
    public void loadMore() {
        if (hasMoreItemToLoad())
            loadMovie(nextPage);
    }

    public void setLoadAdapter(OnLoadAdapter listener) {
        this.onLoadAdapter = listener;
    }

    @Override
    public String toString() {
        return "MoviesAdapter{" +
                "nextPage=" + nextPage +
                ", currentCount=" + currentCount +
                ", totalCount=" + totalCount +
                ", totalPages=" + totalPages +
                '}';
    }

    public static class Options {
        public int roughItemWidthSize = -1;
        public int marginLeft = 0;
        public int marginRight = 0;
        public int marginTop = 0;
        public int radius = 0;
        public int marginBottom = 0;
        public int padding = 0;
        public View emptyView;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_container)
        View container;

        @BindView(R.id.movie_poster)
        CustomImageView moviePoster;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnLoadAdapter {
        void onSuccess();
        void onError(String message);

    }
}
