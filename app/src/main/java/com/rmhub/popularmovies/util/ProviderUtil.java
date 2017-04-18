package com.rmhub.popularmovies.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContentResolverCompat;
import android.util.Log;

import com.rmhub.popularmovies.R;
import com.rmhub.popularmovies.model.MovieDetail;
import com.rmhub.popularmovies.model.ReviewDetail;
import com.rmhub.popularmovies.model.VideoDetail;
import com.rmhub.popularmovies.provider.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by MOROLANI on 4/6/2017
 * <p>
 * owm
 * .
 */

public class ProviderUtil {
    // TODO: 4/6/2017 Check for null variables
    private static final String TAG = ProviderUtil.class.getSimpleName();

    public static List<MovieDetail> insertMovies(Context context, List<MovieDetail> details) {
        if (details == null) return new ArrayList<>();
        List<ContentValues> movieCVs = new ArrayList<>();
        for (MovieDetail movieDetails : details) {

            ContentValues movieCV = new ContentValues();

            movieCV.put(Contract.Movies.COLUMN_AVERAGE_VOTE, movieDetails.getVote_average());

            movieCV.put(Contract.Movies.COLUMN_BACKDROP_URL, movieDetails.getBackdrop_path());

            movieCV.put(Contract.Movies.COLUMN_MOVIE_ID, movieDetails.getMovieID());

            movieCV.put(Contract.Movies.COLUMN_MOVIE_TITLE, movieDetails.getTitle());

            movieCV.put(Contract.Movies.FAVORITE, movieDetails.getFavorite());

            movieCV.put(Contract.Movies.COLUMN_POSTER_URL, movieDetails.getPoster_path());

            movieCV.put(Contract.Movies.COLUMN_PLOT, movieDetails.getOverview());

            movieCV.put(Contract.Movies.COLUMN_RELEASE_DATE, movieDetails.getRelease_date());

            movieCV.put(Contract.Movies.COLUMN_POPULARITY, movieDetails.getPopularity());
            Log.e(TAG, String.valueOf(movieDetails));
            movieCVs.add(movieCV);
        }

        int result = context.getContentResolver()
                .bulkInsert(
                        Contract.Movies.URI,
                        movieCVs.toArray(new ContentValues[movieCVs.size()]));
        if (result > 0) {
            return details;
        }
        return new ArrayList<>();
    }

    public static List<MovieDetail> insertRecommendation(Context context, MovieDetail detail, List<MovieDetail> details) {

        List<ContentValues> recommendationCVs = new ArrayList<>();
        List<ContentValues> movieCVs = new ArrayList<>();

        Log.e(TAG, "Recommended movies for " + String.valueOf(detail));

        for (MovieDetail recommended : details) {
            ContentValues recommendationCV = new ContentValues();
            recommendationCV.put(Contract.Recommendation.COLUMN_MOVIE_ID, detail.getMovieID());
            recommendationCV.put(Contract.Recommendation.COLUMN_RECOMMENDED_ID, recommended.getMovieID());
            recommendationCVs.add(recommendationCV);

            ContentValues movieCV = new ContentValues();
            movieCV.put(Contract.Movies.COLUMN_AVERAGE_VOTE, recommended.getVote_average());
            movieCV.put(Contract.Movies.COLUMN_BACKDROP_URL, recommended.getBackdrop_path());
            movieCV.put(Contract.Movies.COLUMN_MOVIE_ID, recommended.getMovieID());
            movieCV.put(Contract.Movies.COLUMN_MOVIE_TITLE, recommended.getTitle());
            movieCV.put(Contract.Movies.FAVORITE, recommended.getFavorite());
            movieCV.put(Contract.Movies.COLUMN_POSTER_URL, recommended.getPoster_path());
            movieCV.put(Contract.Movies.COLUMN_PLOT, recommended.getOverview());
            movieCV.put(Contract.Movies.COLUMN_RELEASE_DATE, recommended.getRelease_date());
            movieCV.put(Contract.Movies.COLUMN_CATEGORY, recommended.getCategory());
            movieCVs.add(movieCV);

            Log.e(TAG, String.valueOf(recommended));

        }

        int result = context.getContentResolver()
                .bulkInsert(
                        Contract.Recommendation.URI,
                        recommendationCVs.toArray(new ContentValues[recommendationCVs.size()]));

        int result1 = context.getContentResolver()
                .bulkInsert(
                        Contract.Movies.URI,
                        movieCVs.toArray(new ContentValues[movieCVs.size()]));
        if (result > 0 && result1 > 0) {
            return details;
        }
        return new ArrayList<>();
    }

    public static List<ReviewDetail> insertReview(Context context, MovieDetail details, List<ReviewDetail> reviewDetails) {
        List<ContentValues> reviewsCVs = new ArrayList<>();

        for (ReviewDetail movieDetails : reviewDetails) {
            ContentValues reviewCV = new ContentValues();
            reviewCV.put(Contract.Reviews.COLUMN_REVIEW_ID, movieDetails.getId());
            reviewCV.put(Contract.Reviews.COLUMN_REVIEW_URL, movieDetails.getReviewURL());
            reviewCV.put(Contract.Reviews.COLUMN_CONTENT, movieDetails.getContent());
            reviewCV.put(Contract.Reviews.COLUMN_MOVIE_ID, details.getMovieID());
            reviewCV.put(Contract.Reviews.COLUMN_AUTHOR, movieDetails.getAuthor());
            reviewsCVs.add(reviewCV);
        }

        int result = context.getContentResolver()
                .bulkInsert(
                        Contract.Reviews.URI,
                        reviewsCVs.toArray(new ContentValues[reviewsCVs.size()]));
        if (result > 0) {
            return reviewDetails;
        }
        return new ArrayList<>();
    }

    public static List<VideoDetail> insertVideos(Context context, MovieDetail details, List<VideoDetail> reviewDetails) {
        List<ContentValues> reviewsCVs = new ArrayList<>();

        for (VideoDetail movieDetails : reviewDetails) {
            ContentValues reviewCV = new ContentValues();
            reviewCV.put(Contract.Video.COLUMN_NAME, movieDetails.getName());
            reviewCV.put(Contract.Video.COLUMN_SITE, movieDetails.getSite());
            reviewCV.put(Contract.Video.COLUMN_VIDEO_ID, movieDetails.getVideoID());
            reviewCV.put(Contract.Video.COLUMN_SIZE, movieDetails.getSize());
            reviewCV.put(Contract.Video.COLUMN_MOVIE_ID, details.getMovieID());
            reviewCV.put(Contract.Video.COLUMN_TYPE, movieDetails.getType());
            reviewsCVs.add(reviewCV);
        }

        int result = context.getContentResolver()
                .bulkInsert(
                        Contract.Video.URI,
                        reviewsCVs.toArray(new ContentValues[reviewsCVs.size()]));
        if (result > 0) {
            return reviewDetails;
        }
        return new ArrayList<>();
    }

    public static boolean updateFavorite(Context context, int movieID, boolean value) {
        Log.i(ProviderUtil.class.getSimpleName(), "Updating favorite " + value);
        Uri uri = Contract.Movies.URI.buildUpon().appendPath(String.valueOf(movieID)).build();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Movies.FAVORITE, value);
        String selection = Contract.Movies.COLUMN_MOVIE_ID + " = ?";
        String selectionArgs[] = {String.valueOf(movieID)};
        context.getContentResolver().update(uri, contentValues, selection, selectionArgs);

        return markedAsFavorite(context, movieID);

    }

    public static boolean markedAsFavorite(Context context, int movieID) {
        boolean result;

        Uri uri = Contract.Movies.URI.buildUpon().appendPath(String.valueOf(movieID)).build();
        Cursor cursor = context.getContentResolver().query(uri, new String[]{Contract.Movies.FAVORITE}, Contract.Movies.COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(movieID)}, null);
        if (cursor == null || isCursorEmpty(cursor)) {
            return false;
        }
        cursor.moveToFirst();
        result = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Movies.FAVORITE)) == 1;
        cursor.close();
        return result;
    }

    public static ArrayList<MovieDetail> getMovies(Context context) {

        ArrayList<MovieDetail> arrayList = new ArrayList<>();


        Cursor cursor = ContentResolverCompat.query(context.getContentResolver(),
                Contract.Movies.URI,
                null,
                null,
                null,
                null,
                null);

        if (cursor == null || isCursorEmpty(cursor)) {
            return arrayList;
        }
        cursor.moveToFirst();
        while (true) {
            MovieDetail emp = MovieDetail.buildFrom(cursor);
            arrayList.add(emp);
            if (!cursor.moveToNext())
                break;
        }
        cursor.close();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String key = settings.getString(context.getResources().getString(R.string.sort_key), context.getString(R.string.sort_default_value));
        if (key.equalsIgnoreCase(context.getString(R.string.sort_top_rate_value))) {
            Collections.sort(arrayList, new Comparator<MovieDetail>() {
                @Override
                public int compare(MovieDetail o1, MovieDetail o2) {
                    return (int) Math.signum(o2.getVote_average() - o1.getVote_average());
                }
            });
        } else {
            Collections.sort(arrayList, new Comparator<MovieDetail>() {
                @Override
                public int compare(MovieDetail o1, MovieDetail o2) {
                    return (int) Math.signum(o2.getPopularity() - o1.getPopularity());
                }
            });
        }
        return arrayList;
    }

    public static ArrayList<MovieDetail> getFavoriteMovies(Context context) {
        ArrayList<MovieDetail> arrayList = getMovies(context);
        ArrayList<MovieDetail> result = new ArrayList<>();
        for (MovieDetail detail :
                arrayList) {
            if (detail.getFavorite()) {
                result.add(detail);
            }
        }
        return result;
    }

    public static ArrayList<MovieDetail> getRecommendations(Context context, MovieDetail details) {

        Log.e(TAG, "getRecommendations");
        ArrayList<MovieDetail> arrayList = new ArrayList<>();
        Cursor recommendationCursor = ContentResolverCompat.query(context.getContentResolver(),
                Contract.Recommendation.makeWithID(details.getMovieID()),
                null, null, null, null, null);
        if (recommendationCursor == null || isCursorEmpty(recommendationCursor)) {
            details.setRecommendations(arrayList);
            return arrayList;
        }
        recommendationCursor.moveToFirst();
        while (true) {
            int recommendationID = recommendationCursor.getInt(
                    recommendationCursor.getColumnIndexOrThrow(Contract.Recommendation.COLUMN_RECOMMENDED_ID));
            Cursor movieCursor = ContentResolverCompat.query(context.getContentResolver(),
                    Contract.Movies.makeUriForId(recommendationID),
                    null, null, null, null, null);
            if (movieCursor != null && !isCursorEmpty(movieCursor)) {
                movieCursor.moveToFirst();
                arrayList.add(MovieDetail.buildFrom(movieCursor));
                movieCursor.close();
            }
            if (!recommendationCursor.moveToNext())
                break;
        }
        recommendationCursor.close();  for (MovieDetail detail: arrayList){
            Log.e(TAG, String.valueOf(detail));

        }
        details.setRecommendations(arrayList);
        return arrayList;
    }

    public static ArrayList<ReviewDetail> getReviews(Context context, MovieDetail details) {
        Log.e(TAG, "getReviews");
        ArrayList<ReviewDetail> arrayList = new ArrayList<>();
        Cursor cursor = ContentResolverCompat.query(context.getContentResolver(), Contract.Reviews.makeUriWithID(details.getMovieID()),
                null, null, null, null, null);
        if (cursor == null || isCursorEmpty(cursor)) {
            details.setReviews(arrayList);
            return arrayList;
        }

        cursor.moveToFirst();
        while (true) {
            ReviewDetail emp = ReviewDetail.buildFrom(cursor);
            arrayList.add(emp);
            if (!cursor.moveToNext())
                break;
        }
        cursor.close();
        details.setReviews(arrayList);  for (ReviewDetail detail: arrayList){
            Log.e(TAG, String.valueOf(detail));

        }
        return arrayList;
    }

    public static ArrayList<VideoDetail> getVideos(Context context, MovieDetail details) {
        Log.e(TAG, "getVideo");
        ArrayList<VideoDetail> arrayList = new ArrayList<>();
        Cursor cursor = ContentResolverCompat.query(context.getContentResolver(), Contract.Video.makeUriWithID(details.getMovieID()),
                null, null, null, null, null);
        if (cursor == null || isCursorEmpty(cursor)) {
            details.setVideos(arrayList);
            return arrayList;
        }

        cursor.moveToFirst();
        while (true) {
            VideoDetail emp = VideoDetail.buildFrom(cursor);
            arrayList.add(emp);

            if (!cursor.moveToNext())
                break;
        }
        cursor.close();
        for (VideoDetail detail: arrayList){
            Log.e(TAG, String.valueOf(detail));

        }
        details.setVideos(arrayList);
        return arrayList;
    }

    private static boolean isCursorEmpty(Cursor cursor) {
        if (!cursor.moveToFirst() || cursor.getCount() == 0) {
            cursor.close();
            return true;
        }
        return false;
    }
}
