package com.rmhub.popularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


public class MovieProvider extends ContentProvider {

    private static final int MOVIES = 100;
    private static final int MOVIES_FOR_ID = 101;
    private static final int REVIEWS = 200;
    private static final int REVIEWS_FOR_ID = 201;
    private static final int RECOMMENDATION = 300;
    private static final int RECOMMENDATION_FOR_ID = 301;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(Contract.AUTHORITY, Contract.MOVIES, MOVIES);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_MOVIES_WITH_ID, MOVIES_FOR_ID);

        matcher.addURI(Contract.AUTHORITY, Contract.RECOMMENDATION, RECOMMENDATION);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_RECOMMENDATION_WITH_ID, RECOMMENDATION_FOR_ID);

        matcher.addURI(Contract.AUTHORITY, Contract.REVIEWS, REVIEWS);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_REVIEWS_WITH_ID, REVIEWS_FOR_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                returnCursor = db.query(
                        Contract.MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case MOVIES_FOR_ID:
                returnCursor = db.query(
                        Contract.MOVIES,
                        projection,
                        Contract.Movies.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Contract.Movies.getMovieIDFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );

                break;
            case REVIEWS_FOR_ID:
                returnCursor = db.query(
                        Contract.REVIEWS,
                        projection,
                        Contract.Reviews.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Contract.Reviews.getMovieIDFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );

                break;
            case RECOMMENDATION_FOR_ID:
                returnCursor = db.query(
                        Contract.RECOMMENDATION,
                        projection,
                        Contract.Recommendation.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Contract.Recommendation.getMovieIDFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );

                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null) {
            returnCursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                db.insert(
                        Contract.MOVIES,
                        null,
                        values
                );
                returnUri = Contract.Movies.URI;
                break;
            case RECOMMENDATION:
                db.insert(
                        Contract.RECOMMENDATION,
                        null,
                        values
                );
                returnUri = Contract.Recommendation.URI;
                break;
            case REVIEWS:
                db.insert(
                        Contract.REVIEWS,
                        null,
                        values
                );
                returnUri = Contract.Reviews.URI;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                rowsDeleted = db.delete(
                        Contract.MOVIES,
                        selection,
                        selectionArgs
                );

                break;

            case MOVIES_FOR_ID:
                String symbol = Contract.Movies.getMovieIDFromUri(uri);
                rowsDeleted = db.delete(
                        Contract.MOVIES,
                        '"' + symbol + '"' + " =" + Contract.Movies.COLUMN_MOVIE_TITLE,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        if (rowsDeleted != 0) {
            Context context = getContext();
            if (context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnUri;

        switch (uriMatcher.match(uri)) {
            case MOVIES_FOR_ID:
                returnUri = db.update(
                        Contract.MOVIES,
                        values,
                        selection,
                        selectionArgs
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int returnCount;
        Context context;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                returnCount = 0;
                Log.v(getClass().getSimpleName(), "inserting movies in bulk to database");
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long valueReturned = db.insert(
                                Contract.MOVIES,
                                null,
                                value
                        );

                        Log.v(getClass().getSimpleName(), "row id " + valueReturned);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                Log.v(getClass().getSimpleName(), "");
                Log.v(getClass().getSimpleName(), returnCount + " movies inserted");

                return returnCount;
            case RECOMMENDATION:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        returnCount += db.insert(
                                Contract.RECOMMENDATION,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return returnCount;
            case REVIEWS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        returnCount += db.insert(
                                Contract.REVIEWS,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }


    }
}
