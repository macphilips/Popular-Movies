package com.rmhub.popularmovies.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContentResolverCompat;
import android.util.Log;

import com.rmhub.popularmovies.model.MovieDetail;
import com.rmhub.popularmovies.model.ReviewDetails;
import com.rmhub.popularmovies.provider.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOROLANI on 4/6/2017
 * <p>
 * owm
 * .
 */

public class ProviderUtil {
    // TODO: 4/6/2017 Check for null variables

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
        for (MovieDetail recommended : details) {

            ContentValues recommendationCV = new ContentValues();
            recommendationCV.put(Contract.Recommendation.COLUMN_MOVIE_ID, detail.getMovieID());
            recommendationCV.put(Contract.Recommendation.COLUMN_RECOMMENDED_ID, recommended.getMovieID());
            recommendationCVs.add(recommendationCV);

        }

        int result = context.getContentResolver()
                .bulkInsert(
                        Contract.Recommendation.URI,
                        recommendationCVs.toArray(new ContentValues[recommendationCVs.size()]));

        if (result > 0) {
            return details;
        }
        return new ArrayList<>();
    }

    public static List<ReviewDetails> insertReview(Context context, MovieDetail details, List<ReviewDetails> reviewDetails) {
        List<ContentValues> reviewsCVs = new ArrayList<>();

        for (ReviewDetails movieDetails : reviewDetails) {
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

    public static int updateFavorite(Context context, int movieID, boolean value) {
        Uri uri = Contract.Movies.URI.buildUpon().appendPath(String.valueOf(movieID)).build();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Movies.FAVORITE, value);
        String selection = Contract.Movies.COLUMN_MOVIE_ID + " = ?";
        String selectionArgs[] = {String.valueOf(movieID)};
        return context.getContentResolver().update(uri, contentValues, selection, selectionArgs);

    }

    public static List<MovieDetail> getMovies(Context context) {
        Cursor cursor = ContentResolverCompat.query(context.getContentResolver(),
                Contract.Movies.URI,
                null,
                null,
                null,
                null,
                null);

        if (cursor == null || isCursorEmpty(cursor)) {
            Log.d(ProviderUtil.class.getSimpleName(), "Cursor is empty or null");
            return new ArrayList<>();
        }
        cursor.moveToFirst();
        List<MovieDetail> arrayList = new ArrayList<>();
        while (true) {
            MovieDetail emp = MovieDetail.buildFrom(cursor);
            arrayList.add(emp);
            if (!cursor.moveToNext())
                break;
        }
        cursor.close();
        return arrayList;
    }

    public static int getCategory(Context context, int movieID) {
        Cursor cursor = ContentResolverCompat.query(context.getContentResolver(),
                Contract.Movies.URI,
                new String[]{Contract.Movies.COLUMN_CATEGORY},
                Contract.Movies.COLUMN_MOVIE_ID + " =?",
                new String[]{String.valueOf(movieID)},
                null,
                null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Movies.COLUMN_CATEGORY));
        }
        return -1;
    }

    public static List<MovieDetail> getRecommendation(Context context, MovieDetail details) {

        Cursor recommendationCursor = ContentResolverCompat.query(context.getContentResolver(),
                Contract.Recommendation.makeWithID(details.getMovieID()),
                null, null, null, null, null);
        if (recommendationCursor == null) {
            return new ArrayList<>();
        }
        recommendationCursor.moveToFirst();
        List<MovieDetail> arrayList = new ArrayList<>();
        while (true) {
            int recommendationID = recommendationCursor.getInt(
                    recommendationCursor.getColumnIndexOrThrow(Contract.Recommendation.COLUMN_RECOMMENDED_ID));
            Cursor movieCursor = ContentResolverCompat.query(context.getContentResolver(),
                    Contract.Movies.makeUriForId(recommendationID),
                    null, null, null, null, null);
            if (movieCursor == null) continue;
            movieCursor.moveToFirst();
            arrayList.add(MovieDetail.buildFrom(movieCursor));
            movieCursor.close();
            if (!recommendationCursor.moveToNext())
                break;
        }
        recommendationCursor.close();
        return arrayList;
    }

    public static List<ReviewDetails> getReviews(Context context, MovieDetail details) {
        Cursor cursor = ContentResolverCompat.query(context.getContentResolver(), Contract.Reviews.makeUriWithID(details.getMovieID()),
                null, null, null, null, null);
        if (cursor == null) {
            return new ArrayList<>();
        }

        cursor.moveToFirst();
        List<ReviewDetails> arrayList = new ArrayList<>();
        while (true) {
            ReviewDetails emp = ReviewDetails.buildFrom(cursor);
            arrayList.add(emp);
            if (!cursor.moveToNext())
                break;
        }
        cursor.close();
        return arrayList;
    }

    public static boolean isCursorEmpty(Cursor cursor) {
        if (!cursor.moveToFirst() || cursor.getCount() == 0) return true;
        return false;
    }
}
