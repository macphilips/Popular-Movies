package com.rmhub.popularmovies.helper;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.rmhub.popularmovies.provider.Contract;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class Review implements Parcelable {

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
    private String id;
    private String author;
    private String content;
    private String reviewURL;

    protected Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        reviewURL = in.readString();
    }

    protected Review() {

    }

    public static Review buildFrom(Cursor cursor) {
        Review review = new Review();
        review.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Reviews.COLUMN_AUTHOR)));
        review.setContent(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Reviews.COLUMN_CONTENT)));
        review.setId(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Reviews.COLUMN_REVIEW_ID)));
        review.setReviewURL(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Reviews.COLUMN_REVIEW_URL)));
        return review;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(reviewURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReviewURL() {
        return reviewURL;
    }

    public void setReviewURL(String reviewURL) {
        this.reviewURL = reviewURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
