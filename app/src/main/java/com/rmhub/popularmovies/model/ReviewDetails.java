package com.rmhub.popularmovies.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rmhub.popularmovies.provider.Contract;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

public class ReviewDetails implements Parcelable {

    public static final Creator<ReviewDetails> CREATOR = new Creator<ReviewDetails>() {
        @Override
        public ReviewDetails createFromParcel(Parcel in) {
            return new ReviewDetails(in);
        }

        @Override
        public ReviewDetails[] newArray(int size) {
            return new ReviewDetails[size];
        }
    };

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("url")
    @Expose
    private String reviewURL;

    protected ReviewDetails(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        reviewURL = in.readString();
    }

    public ReviewDetails() {

    }

    public static ReviewDetails buildFrom(Cursor cursor) {
        ReviewDetails reviewDetail = new ReviewDetails();
        reviewDetail.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Reviews.COLUMN_AUTHOR)));
        reviewDetail.setContent(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Reviews.COLUMN_CONTENT)));
        reviewDetail.setId(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Reviews.COLUMN_REVIEW_ID)));
        reviewDetail.setReviewURL(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Reviews.COLUMN_REVIEW_URL)));
        return reviewDetail;
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
