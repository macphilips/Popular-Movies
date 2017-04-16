package com.rmhub.popularmovies.provider;


import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {

    static final String AUTHORITY = "com.rmhub.popularmovies";
    static final String MOVIES = "movies";
    static final String REVIEWS = "reviews";
    static final String RECOMMENDATION = "recommendations";
    static final String VIDEOS = "videos";

    static final String PATH_VIDEOS_WITH_ID = VIDEOS + "/#";
    static final String PATH_MOVIES_WITH_ID = MOVIES + "/#";
    static final String PATH_REVIEWS_WITH_ID = REVIEWS + "/#";
    static final String PATH_RECOMMENDATION_WITH_ID = RECOMMENDATION + "/#";

    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }

    @SuppressWarnings("unused")
    public static final class Movies implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(MOVIES).build();

        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RELEASE_DATE = "date";
        public static final String COLUMN_AVERAGE_VOTE = "vote_average";
        public static final String FAVORITE = "fav";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_POPULARITY = "popularity";

        /*public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_MOVIE_TITLE,
                COLUMN_BACKDROP_URL,
                COLUMN_POSTER_URL,
                COLUMN_MOVIE_ID,
                COLUMN_PLOT
        );*/

        public static Uri makeUriForId(int symbol) {
            return URI.buildUpon().appendPath(String.valueOf(symbol)).build();
        }

        static String getMovieIDFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

    public static final class Reviews implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(REVIEWS).build();

        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_REVIEW_URL = "review_url";
        public static String FAVORITE = "fav";
        /*public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_MOVIE_TITLE,
                COLUMN_BACKDROP_URL,
                COLUMN_POSTER_URL,
                COLUMN_MOVIE_ID,
                COLUMN_PLOT
        );*/

        public static Uri makeUriWithID(int symbol) {
            return URI.buildUpon().appendPath(String.valueOf(symbol)).build();
        }

        static String getMovieIDFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

    public static final class Recommendation implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(RECOMMENDATION).build();

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_RECOMMENDED_ID = "recommended_id";

        /*public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_MOVIE_TITLE,
                COLUMN_BACKDROP_URL,
                COLUMN_POSTER_URL,
                COLUMN_MOVIE_ID,
                COLUMN_PLOT
        );*/

        public static Uri makeWithID(int id) {
            return URI.buildUpon().appendPath(String.valueOf(id)).build();
        }

        static String getMovieIDFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

    public static final class Video implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(VIDEOS).build();

        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        /*public static final ImmutableList<String> QUOTE_COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_MOVIE_TITLE,
                COLUMN_BACKDROP_URL,
                COLUMN_POSTER_URL,
                COLUMN_MOVIE_ID,
                COLUMN_PLOT
        );*/

        public static Uri makeWithID(int id) {
            return URI.buildUpon().appendPath(String.valueOf(id)).build();
        }

        static String getMovieIDFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

}
