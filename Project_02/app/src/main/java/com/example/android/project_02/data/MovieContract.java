package com.example.android.project_02.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database. This class is not necessary, but keeps
 * the code organized.
 */
public class MovieContract {

    // Main package of the content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.project_02";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider for project_02.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path to the movie content provider
    public static final String PATH_MOVIE = "movie";

    // Inner class that defines the table contents of the movie table
    public static final class MovieEntry implements BaseColumns {

        // The base CONTENT_URI used to query the Movie table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        // Used internally as the name of our movie table.
        public static final String TABLE_NAME = "favorite_movie";

        // Name of the TITLE column
        public static final String COLUMN_TITLE = "title";
    }
}