package com.rmhub.popularmovies.model;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rmhub.popularmovies.provider.Contract;

import java.util.ArrayList;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class MovieDetail implements Parcelable {

    private static final String BASE_PATH = "https://image.tmdb.org/t/p";
    private static final String POSTER_SIZE_SMALL = "/w92/";
    private static final String POSTER_SIZE = "/w185/";
    private static final String BACKDROP_SIZE = "/w780/";
    @SerializedName("video")
    @Expose
    private
    boolean video;
    @SerializedName("belongs_to_collection")
    @Expose
    private
    String belongs_to_collection;
    @SerializedName("homepage")
    @Expose
    private
    String homepage;
    @SerializedName("imdb_id")
    @Expose
    private
    String imdb_id;
    @SerializedName("original_language")
    @Expose
    private String original_language;
    @SerializedName("original_title")
    @Expose
    private
    String original_title;
    @SerializedName("overview")
    @Expose
    private
    String overview;
    @SerializedName("poster_path")
    @Expose
    private
    String poster_path;
    @SerializedName("release_date")
    @Expose
    private
    String release_date;
    @SerializedName("status")
    @Expose
    private
    String status;
    @SerializedName("tagline")
    @Expose
    private
    String tagline;
    @SerializedName("title")
    @Expose
    private
    String title;
    @SerializedName("revenue")
    @Expose
    private
    long revenue;
    @SerializedName("runtime")
    @Expose
    private
    int runtime;
    @SerializedName("vote_count")
    @Expose
    private
    int vote_count;
    @SerializedName("vote_average")
    @Expose
    private
    double vote_average;
    @SerializedName("adult")
    @Expose
    private boolean adult;
    @SerializedName("backdrop_path")
    @Expose
    private String backdrop_path;
    @SerializedName("budget")
    @Expose
    private long budget;
    @SerializedName("id")
    @Expose
    private int movieID;
    @SerializedName("popularity")
    @Expose
    private double popularity;

    private boolean favorite;
    private int category;

    public static final Creator<MovieDetail> CREATOR = new Creator<MovieDetail>() {
        @Override
        public MovieDetail createFromParcel(Parcel in) {
            return new MovieDetail(in);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };
    private ArrayList<MovieDetail> recommendations;
    private ArrayList<ReviewDetail> reviews;
    private ArrayList<VideoDetail> videos;

    protected MovieDetail(Parcel in) {
        video = in.readByte() != 0;
        belongs_to_collection = in.readString();
        homepage = in.readString();
        imdb_id = in.readString();
        original_language = in.readString();
        original_title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        release_date = in.readString();
        status = in.readString();
        tagline = in.readString();
        title = in.readString();
        revenue = in.readLong();
        runtime = in.readInt();
        vote_count = in.readInt();
        vote_average = in.readDouble();
        adult = in.readByte() != 0;
        backdrop_path = in.readString();
        budget = in.readLong();
        movieID = in.readInt();
        popularity = in.readDouble();
        favorite = in.readByte() != 0;
        category = in.readInt();
        recommendations = in.createTypedArrayList(MovieDetail.CREATOR);
        reviews = in.createTypedArrayList(ReviewDetail.CREATOR);
        videos = in.createTypedArrayList(VideoDetail.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public MovieDetail() {
    }

    public static MovieDetail buildFrom(Cursor cursor) {
        MovieDetail details = new MovieDetail();
        try {
            details.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Movies.COLUMN_MOVIE_TITLE)));

            details.setRelease_date(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Movies.COLUMN_RELEASE_DATE)));

            details.setVote_average(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Movies.COLUMN_AVERAGE_VOTE))));

            details.setPoster_path(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Movies.COLUMN_POSTER_URL)));

            details.setBackdrop_path(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Movies.COLUMN_BACKDROP_URL)));

            details.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Movies.COLUMN_PLOT)));

            details.setMovieID((cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Movies.COLUMN_MOVIE_ID))));

            details.setPopularity((cursor.getDouble(cursor.getColumnIndexOrThrow(Contract.Movies.COLUMN_POPULARITY))));

            details.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Movies.FAVORITE)) == 1);


        } catch (CursorIndexOutOfBoundsException exception) {
            exception.printStackTrace();
        }
        return details;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeString(belongs_to_collection);
        dest.writeString(homepage);
        dest.writeString(imdb_id);
        dest.writeString(original_language);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(poster_path);
        dest.writeString(release_date);
        dest.writeString(status);
        dest.writeString(tagline);
        dest.writeString(title);
        dest.writeLong(revenue);
        dest.writeInt(runtime);
        dest.writeInt(vote_count);
        dest.writeDouble(vote_average);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(backdrop_path);
        dest.writeLong(budget);
        dest.writeInt(movieID);
        dest.writeDouble(popularity);
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeInt(category);
        dest.writeTypedList(recommendations);
        dest.writeTypedList(reviews);
        dest.writeTypedList(videos);
    }

    public ArrayList<MovieDetail> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(ArrayList<MovieDetail> recommendations) {
        this.recommendations = recommendations;
    }

    public ArrayList<ReviewDetail> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<ReviewDetail> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<VideoDetail> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<VideoDetail> videos) {
        this.videos = videos;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public String getBackdropURL() {
        return BASE_PATH + BACKDROP_SIZE + backdrop_path;
    }
    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getBelongs_to_collection() {
        return belongs_to_collection;
    }

    public void setBelongs_to_collection(String belongs_to_collection) {
        this.belongs_to_collection = belongs_to_collection;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterURL() {
        return BASE_PATH + POSTER_SIZE + poster_path;
    }

    public String getPoster_path() {
        return  poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getPoster_path_small() {
        return BASE_PATH + POSTER_SIZE_SMALL + poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public long getRevenue() {
        return revenue;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "MovieDetail{" +
                "backdrop_path='" + backdrop_path + '\'' +
                ", poster_path='" + poster_path + '\'' +
                ", title='" + title + '\'' +
                ", movieID=" + movieID +
                ", vote_average=" + vote_average +
                '}';
    }

}
