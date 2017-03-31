package com.rmhub.popularmovies.helper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class MovieDetails implements Parcelable {

    public static final Creator<MovieDetails> CREATOR = new Creator<MovieDetails>() {
        @Override
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        @Override
        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };
    private static final String BASE_PATH = "https://image.tmdb.org/t/p";
    private static final String POSTER_SIZE_SMALL = "/w92/";
    private static final String POSTER_SIZE = "/w185/";
    private static final String BACKDROP_SIZE = "/w1280/";
    private List<Genre> genres;
    private boolean adult, video;
    private String backdrop_path, belongs_to_collection,
            homepage, imdb_id, original_language, original_title, overview,
            poster_path, release_date, status,
            tagline, title;
    private long budget, revenue;
    private int id, runtime, vote_count;
    private double popularity, vote_average;
    private int rgb;
    private int titleTextColor;

    public MovieDetails() {

    }

    protected MovieDetails(Parcel in) {
        genres = in.createTypedArrayList(Genre.CREATOR);
        adult = in.readByte() != 0;
        video = in.readByte() != 0;
        backdrop_path = in.readString();
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
        budget = in.readLong();
        revenue = in.readLong();
        id = in.readInt();
        runtime = in.readInt();
        vote_count = in.readInt();
        popularity = in.readDouble();
        vote_average = in.readDouble();
        rgb = in.readInt();
        titleTextColor = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(genres);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeString(backdrop_path);
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
        dest.writeLong(budget);
        dest.writeLong(revenue);
        dest.writeInt(id);
        dest.writeInt(runtime);
        dest.writeInt(vote_count);
        dest.writeDouble(popularity);
        dest.writeDouble(vote_average);
        dest.writeInt(rgb);
        dest.writeInt(titleTextColor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
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

    public String getBackdrop_path() {
        return BASE_PATH + BACKDROP_SIZE + backdrop_path;
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

    public String getPoster_path() {
        return BASE_PATH + POSTER_SIZE + poster_path;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getRgb() {
        return rgb;
    }

    public void setRgb(int rgb) {
        this.rgb = rgb;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }
}