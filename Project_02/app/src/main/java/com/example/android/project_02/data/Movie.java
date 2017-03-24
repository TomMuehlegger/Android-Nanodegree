package com.example.android.project_02.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Movie class representing a movie
 */
public class Movie implements Parcelable {
    private final int id;
    private final String originalTitle;
    private final String posterUrl;
    private final String overview;
    private final double userRating;
    private final Date releaseDate;

    public Movie(int id, String originalTitle, String posterUrl, String overview, double userRating, Date releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterUrl = posterUrl;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        originalTitle = in.readString();
        posterUrl = in.readString();
        overview = in.readString();
        userRating = in.readDouble();
        // Possible, because Date implements Serializable
        releaseDate = (Date)in.readSerializable();
    }

    public int getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getOverview() {
        return overview;
    }

    public double getUserRating() {
        return userRating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(originalTitle);
        dest.writeString(posterUrl);
        dest.writeString(overview);
        dest.writeDouble(userRating);
        dest.writeSerializable(releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
