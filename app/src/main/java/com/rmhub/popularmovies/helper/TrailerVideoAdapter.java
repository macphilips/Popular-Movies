package com.rmhub.popularmovies.helper;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.model.MovieDetail;
import com.rmhub.popularmovies.model.Video;
import com.rmhub.popularmovies.model.VideoDetail;
import com.rmhub.popularmovies.util.MovieRequest;
import com.rmhub.popularmovies.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOROLANI on 4/8/2017
 * <p>
 * owm
 * .
 */

public class TrailerVideoAdapter extends PagerAdapter {
    private final LayoutInflater mInflater;
    private final List<VideoDetail> videoDetails = new ArrayList<>();
    private final Context mCtx;
    private View.OnClickListener mPlayListener;
    private View.OnClickListener mShareListener;


    public TrailerVideoAdapter(final MovieDetail details, Context context) {
        mCtx = context;
        mInflater = LayoutInflater.from(context);

        Bundle bundle = new Bundle();
        bundle.putString(MovieRequest.QUERY_URL, NetworkUtil.buildMovieVideos(details, 1));
        bundle.putParcelable(MovieRequest.MOVIE_DETAILS, details);
        Video.Query videoQuery = new Video.Query(bundle);
        NetworkUtil.getInstance(context).fetchResult(videoQuery, Video.Result.class, new MovieRequest.MovieRequestListener<Video.Result>() {
            @Override
            public void onResponse(Video.Result result) {
                super.onResponse(result);
                addVideoDetail(result.getVideoDetails());
            }
        });
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    public void setPlayListener(View.OnClickListener mListener) {
        this.mPlayListener = mListener;
    }

    @Override
    public int getCount() {
        return videoDetails.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = mInflater.inflate(R.layout.trailer_item, container, false);
        final View playButton = v.findViewById(R.id.trailer_icon);
        playButton.setTag(videoDetails.get(position));
        playButton.setOnClickListener(mPlayListener);
        final View progressBar = v.findViewById(R.id.progressBar);
        TextView title = (TextView) v.findViewById(R.id.title);
        v.findViewById(R.id.share).setTag(videoDetails.get(position));
        v.findViewById(R.id.share).setOnClickListener(mShareListener);
        title.setText(videoDetails.get(position).getName());
        Glide
                .with(mCtx)
                .load(NetworkUtil.buildYoutubeVideoThumbnailURL(videoDetails.get(position).getVideoID()))
                .fitCenter()
                .placeholder(R.drawable.post_background)
                .crossFade().listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        })
                .into((ImageView) v.findViewById(R.id.trailer_thumbnail));
        container.addView(v);
        return v;
    }


    public void addVideoDetail(List<VideoDetail> details) {
        this.videoDetails.clear();
        if (details != null && !details.isEmpty()) {
            this.videoDetails.addAll(details);
        }
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    public void setShareListener(View.OnClickListener mShareListener) {
        this.mShareListener = mShareListener;
    }
}
