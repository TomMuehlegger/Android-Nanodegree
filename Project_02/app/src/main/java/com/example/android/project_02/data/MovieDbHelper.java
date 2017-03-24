package com.example.android.project_02.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movie data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // Name of the local SQLite database
    public static final String DATABASE_NAME = "movie.db";

    // Increase version when changing the database schema
    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase - The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our movie data.
         */
        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +

                // Add the COLUMNS to create
                MovieContract.MovieEntry._ID                 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_TITLE        + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_OVERVIEW     + " TEXT," +
                MovieContract.MovieEntry.COLUMN_POSTER_URL   + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER," +
                MovieContract.MovieEntry.COLUMN_USER_RATING  + " REAL);";

        // Create the movie table
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    /**
     * Method called on updating the database scheme
     *
     * @param sqLiteDatabase - Database that is being upgraded
     * @param oldVersion - The old database version
     * @param newVersion - The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}