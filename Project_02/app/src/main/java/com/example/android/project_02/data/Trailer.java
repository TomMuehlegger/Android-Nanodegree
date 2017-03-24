package com.example.android.project_02.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Trailer class representing a movie trailer
 */
public class Trailer implements Parcelable {
    private final String id;
    private final String name;
    private final String key;

    public Trailer(String id, String name, String key) {
        this.id = id;
        this.name = name;
        this.key = key;
    }

    protected Trailer(Parcel in) {
        id = in.readString();
        name = in.readString();
        key = in.readString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(key);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
