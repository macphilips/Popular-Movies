package com.rmhub.popularmovies.helper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MOROLANI on 3/27/2017
 * <p>
 * owm
 * .
 */

class Genre implements Parcelable{
    public static final Creator<Genre> CREATOR = new Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };
    private int id;
    private String name;

    protected Genre() {

    }

    protected Genre(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
