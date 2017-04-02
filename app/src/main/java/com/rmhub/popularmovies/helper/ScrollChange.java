package com.rmhub.popularmovies.helper;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.rmhub.simpleimagefetcher.ImageFetcher;
import com.rmhub.simpleimagefetcher.Utils;

/**
 * Created by MOROLANI on 3/7/2017
 * <p>
 * owm
 * .
 */

public class ScrollChange extends RecyclerView.OnScrollListener {

    private static int page_num = 1;
    private final RecyclerView.LayoutManager manager;
    private final LoadMoreCallback callback;
    private boolean loading;
    private ImageFetcher mImageFetcher;

    public ScrollChange(RecyclerView.LayoutManager mAdapter, LoadMoreCallback callback) {
        this.manager = mAdapter;
        this.callback = callback;
    }

    public void setLoading() {
        this.loading = false;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        Log.d(getClass().getSimpleName(), "New State " + newState);
        if (mImageFetcher != null) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                // Before Honeycomb pause image loading on scroll to help with performance
                if (!Utils.hasHoneycomb()) {
                    mImageFetcher.setPauseWork(true);
                }
            } else {
                mImageFetcher.setPauseWork(false);
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int totalItemCount;
        int lastVisibleItem;
        int visibleThreshold;
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            totalItemCount = gridLayoutManager.getItemCount();
            lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
            visibleThreshold = gridLayoutManager.getChildCount();

        } else {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;
            totalItemCount = linearLayoutManager.getItemCount();
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            visibleThreshold = linearLayoutManager.getChildCount();
        }
        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            if (callback != null) {
                callback.loadMore(++page_num);
            }
            loading = true;
        }
    }

    public void setImageFetcher(ImageFetcher imageFetcher) {
        this.mImageFetcher = imageFetcher;
    }

    public void reset() {
        page_num = 1;
    }

}
