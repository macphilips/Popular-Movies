package com.rmhub.popularmovies.helper;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by MOROLANI on 3/7/2017
 * <p>
 * owm
 * .
 */

public class ScrollChange extends RecyclerView.OnScrollListener {
    private final RecyclerView.LayoutManager manager;
    private final LoadMoreCallback callback;
    private boolean loading;

    public ScrollChange(RecyclerView.LayoutManager mAdapter, LoadMoreCallback callback) {
        this.manager = mAdapter;
        this.callback = callback;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
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
                callback.loadMore();
            }
            loading = true;
        }
    }

    public void setLoading() {
        loading = false;
    }
}
