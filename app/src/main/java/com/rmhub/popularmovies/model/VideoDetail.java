package com.rmhub.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MOROLANI on 4/8/2017
 * <p>
 * owm
 * .
 */

public class VideoDetail implements Parcelable {
    public static final Creator<VideoDetail> CREATOR = new Creator<VideoDetail>() {
        @Override
        public VideoDetail createFromParcel(Parcel in) {
            return new VideoDetail(in);
        }

        @Override
        public VideoDetail[] newArray(int size) {
            return new VideoDetail[size];
        }
    };
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("key")
    @Expose
    private String videoID;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("size")
    @Expose
    private int size;

    public VideoDetail() {

    }

    public VideoDetail(Parcel in) {
        id = in.readString();
        videoID = in.readString();
        type = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(videoID);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
