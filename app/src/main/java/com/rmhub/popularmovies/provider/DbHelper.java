package com.rmhub.popularmovies.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


class DbHelper extends SQLiteOpenHelper {


    private static final String NAME = "popularmovies.db";
    private static final int VERSION = 1;


    DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String movie_table = "CREATE TABLE " + Contract.MOVIES + " ("
                + Contract.Movies.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + Contract.Movies.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + Contract.Movies.COLUMN_BACKDROP_URL + " TEXT NOT NULL, "
                + Contract.Movies.COLUMN_POSTER_URL + " TEXT NOT NULL, "
                + Contract.Movies.COLUMN_PLOT + " TEXT NOT NULL, "
                + Contract.Movies.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + Contract.Movies.COLUMN_AVERAGE_VOTE + " TEXT NOT NULL, "
                + Contract.Movies.COLUMN_CATEGORY + " INTEGER NOT NULL DEFAULT \'0\', "
                + Contract.Movies.COLUMN_POPULARITY + " REAL NOT NULL DEFAULT \'0\', "
                + Contract.Movies.FAVORITE + " INTEGER NOT NULL DEFAULT \'0\'" +
                ");";

        String review_table = "CREATE TABLE " + Contract.REVIEWS + " ("
                + Contract.Reviews._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.Reviews.COLUMN_CONTENT + " TEXT NOT NULL, "
                + Contract.Reviews.COLUMN_REVIEW_ID + " TEXT NOT NULL, "
                + Contract.Reviews.COLUMN_REVIEW_URL + " TEXT NOT NULL, "
                + Contract.Reviews.COLUMN_MOVIE_ID + " INTEGER references " + Contract.MOVIES + " (" + Contract.Movies.COLUMN_MOVIE_ID + "), "
                + Contract.Reviews.COLUMN_AUTHOR + " TEXT NOT NULL "
                + ");";

        String video_table = "CREATE TABLE " + Contract.VIDEOS + " ("
                + Contract.Reviews._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.Video.COLUMN_VIDEO_ID + " TEXT NOT NULL, "
                + Contract.Video.COLUMN_SITE + " TEXT NOT NULL, "
                + Contract.Video.COLUMN_NAME + " TEXT NOT NULL, "
                + Contract.Video.COLUMN_SIZE + " INTEGER NOT NULL, "
                + Contract.Video.COLUMN_MOVIE_ID + " INTEGER references " + Contract.MOVIES + " (" + Contract.Movies.COLUMN_MOVIE_ID + "), "
                + Contract.Video.COLUMN_TYPE + " TEXT NOT NULL "
                + ");";


        String recommendation_table = "CREATE TABLE " + Contract.RECOMMENDATION + " ("
                + Contract.Recommendation._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contract.Recommendation.COLUMN_RECOMMENDED_ID + " INTEGER NOT NULL, "
                + Contract.Recommendation.COLUMN_MOVIE_ID + " INTEGER references " + Contract.MOVIES + " (" + Contract.Movies.COLUMN_MOVIE_ID + "), "
                + "UNIQUE (" + Contract.Recommendation.COLUMN_RECOMMENDED_ID + "," + Contract.Recommendation.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


        db.execSQL(movie_table);
        db.execSQL(review_table);
        db.execSQL(recommendation_table);
        db.execSQL(video_table);

        Log.v(getClass().getSimpleName(),"onCreate database");

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            Log.v(getClass().getSimpleName(),"Foreign key contraints enabled");
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + Contract.MOVIES);
        db.execSQL(" DROP TABLE IF EXISTS " + Contract.RECOMMENDATION);
        db.execSQL(" DROP TABLE IF EXISTS " + Contract.REVIEWS);

        onCreate(db);
    }
}
