package com.rmhub.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rmhub.simpleimagefetcher.ImageFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.MyViewHolder> {
    private static List<MovieDetails> movieList = null;
    private final Context mContext;
    private final ImageFetcher mImageFetcher;
    private int currentCount;
    private int totalCount;
    private int numColumns;

    public PopularMoviesAdapter(Context mContext, ImageFetcher imageFetcher) {
        this.mContext = mContext;
        movieList = new ArrayList<>();
        this.mImageFetcher = imageFetcher;
    }

    public void addMovieList(List<MovieDetails> movieList) {
        int end = PopularMoviesAdapter.movieList.size();
        for (int i = 0, n = movieList.size(); i < n; i++) {
            movieList.get(i).setId(i + end + 1);
        }
        PopularMoviesAdapter.movieList.addAll(movieList);
        currentCount = PopularMoviesAdapter.movieList.size();
        // notifyDataSetChanged();
        notifyItemChanged(currentCount);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ///Picasso.with(mContext).load(movieList.get(position).getPoster_path()).into(holder.moviePoster);
        mImageFetcher.loadImage(movieList.get(position).getPoster_path(), holder.moviePoster);
    }

    @Override
    public int getItemCount() {

        return (movieList == null ||numColumns == 0) ? 0 : movieList.size();
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

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView moviePoster;

        MyViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
        }
    }


}
