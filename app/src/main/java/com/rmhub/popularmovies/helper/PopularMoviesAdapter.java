package com.rmhub.popularmovies.helper;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rmhub.popularmovies.R;
import com.rmhub.simpleimagefetcher.ImageFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.MyViewHolder> {
    private static List<MovieDetails> movieList = null;
    private final Context mContext;
    private ImageFetcher mImageFetcher;
    private int currentCount;
    private int totalCount;
    private int numColumns;
    private View.OnClickListener listener;
    private int itemWidth;
    private LinearLayout.LayoutParams mImageViewLayoutParams;

    public PopularMoviesAdapter(Context mContext, ImageFetcher imageFetcher) {
        this.mContext = mContext;
        movieList = new ArrayList<>();
        this.mImageFetcher = imageFetcher;
        mImageViewLayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public PopularMoviesAdapter(Context mContext) {
        this(mContext, null);
    }

    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    public void setImageFetcher(ImageFetcher mImageFetcher) {
        this.mImageFetcher = mImageFetcher;
    }

    public void clearAdapter() {
        movieList.clear();
        currentCount = 0;
        //    numColumns = 0;
        notifyDataSetChanged();
    }

    public void addMovieList(List<MovieDetails> movieList) {
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
        holder.moviePoster.setTag(tag);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.moviePoster.setTransitionName(String.format(Locale.US, "poster_%d", tag.getId()));
        }

        holder.moviePoster.setOnClickListener(listener);
        holder.moviePoster.setLayoutParams(mImageViewLayoutParams);
        if (mImageFetcher != null)
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
        mImageViewLayoutParams =
                new LinearLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        notifyDataSetChanged();

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView moviePoster;

        MyViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
        }
    }


}
