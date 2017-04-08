package com.rmhub.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContentResolverCompat;
import android.util.Log;

import com.rmhub.popularmovies.helper.MovieDetails;
import com.rmhub.popularmovies.helper.ReviewDetails;
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

    public static List<MovieDetails> insertMovies(Context context, List<MovieDetails> details) {
        if (details == null) return new ArrayList<>();
        List<ContentValues> movieCVs = new ArrayList<>();

        for (MovieDetails movieDetails : details) {
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

    public static List<MovieDetails> insertRecommendation(Context context, MovieDetails detail, List<MovieDetails> details) {

        List<ContentValues> movieCVs = new ArrayList<>();
        List<ContentValues> recommendationCVs = new ArrayList<>();
        for (MovieDetails recommended : details) {

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

    public static List<ReviewDetails> insertReview(Context context, MovieDetails details, List<ReviewDetails> reviewDetails) {
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
                        Contract.Reviews.makeUriWithID(details.getMovieID()),
                        reviewsCVs.toArray(new ContentValues[reviewsCVs.size()]));
        if (result > 0) {
            return reviewDetails;
        }
        return new ArrayList<>();
    }

    public static int updateFavorite(Context context, String movieID, boolean value) {
        Uri uri = Contract.Movies.URI.buildUpon().appendPath(movieID).build();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Movies.FAVORITE, value);
        String selection = Contract.Movies.COLUMN_MOVIE_ID + " = ?";
        String selectionArgs[] = {movieID};
        return context.getContentResolver().update(uri, contentValues, selection, selectionArgs);

    }

    public static List<MovieDetails> getMovies(Context context) {
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
        List<MovieDetails> arrayList = new ArrayList<>();
        while (true) {
            MovieDetails emp = MovieDetails.buildFrom(cursor);
            arrayList.add(emp);
            if (!cursor.moveToNext())
                break;
        }
        cursor.close();
        return arrayList;
    }

    public static List<MovieDetails> getRecommendation(Context context, MovieDetails details) {

        Cursor recommendationCursor = ContentResolverCompat.query(context.getContentResolver(),
                Contract.Recommendation.makeWithID(details.getMovieID()),
                null, null, null, null, null);
        if (recommendationCursor == null) {
            return new ArrayList<>();
        }
        recommendationCursor.moveToFirst();
        List<MovieDetails> arrayList = new ArrayList<>();
        while (true) {
            int recommendationID = recommendationCursor.getInt(
                    recommendationCursor.getColumnIndexOrThrow(Contract.Recommendation.COLUMN_RECOMMENDED_ID));
            Cursor movieCursor = ContentResolverCompat.query(context.getContentResolver(),
                    Contract.Movies.makeUriForId(recommendationID),
                    null, null, null, null, null);
            if (movieCursor == null) continue;
            movieCursor.moveToFirst();
            arrayList.add(MovieDetails.buildFrom(movieCursor));
            movieCursor.close();
            if (!recommendationCursor.moveToNext())
                break;
        }
        recommendationCursor.close();
        return arrayList;
    }

    public static List<ReviewDetails> getReviews(Context context, MovieDetails details) {
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
        details.setReviewDetails(arrayList);
        cursor.close();
        return arrayList;
    }

    public static boolean isCursorEmpty(Cursor cursor) {
        if (!cursor.moveToFirst() || cursor.getCount() == 0) return true;
        return false;
    }
}
