package com.example.android.project_02.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;


/**
 * MovieProvider class extending the ContentProvider to store movies
 * in an SQLite database. Furthermore this content provider could be accessed from
 * any app on the entire phone.
 */
public class MovieProvider extends ContentProvider {

    /*
     * These constant will be used to match URIs with the data they are looking for. We will take
     * advantage of the UriMatcher class to make that matching MUCH easier than doing something
     * ourselves, such as using regular expressions.
     */
    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    /*
     * The URI Matcher used by this content provider. s stands for a static member variable
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    /**
     * Method to build a URI matcher to check the type of a URI
     *
     * @return - UriMatcher to check the URI type
     */
    public static UriMatcher buildUriMatcher() {

        // Create a new URI matcher with no_match at default
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;


        // This URI is content://com.example.android.project_02/movie/
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);

        // This URI is content://com.example.android.project_02/movie/123
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

        return matcher;
    }

    /**
     * Method called when the content provider is created.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        /*
         * As noted in the comment above, onCreate is run on the main thread, so performing any
         * lengthy operations will cause lag in your app. Since MovieDbHelper's constructor is
         * very lightweight, we are safe to perform that initialization here.
         */
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /**
     * Method to insert multiple movies to the database
     *
     * @param uri - URI for the insert operation
     * @param values - values to insert to the database
     * @return - number of inserted rows
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        throw new RuntimeException("Inserting multiple movies not implemented");
    }

    /**
     * Method to get data from the database based on a selection string and selection arguments.
     * Furthermore there is a possibility to order the items by condition.
     *
     * @param uri - URI for the query operation
     * @param projection - Elements to get from the database scheme
     * @param selection - Selection for the query
     * @param selectionArgs - Selection arguments to filter data
     * @param sortOrder - Sort order to sort the items
     * @return - a cursor to handle the requested data items
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;

        // Switch statement to decide wich query shoud be executed
        switch (sUriMatcher.match(uri)) {

            // Return the movie with a specified id from the database
            case CODE_MOVIE_WITH_ID: {

                cursor = mOpenHelper.getReadableDatabase().query(
                        // Table we are going to query
                        MovieContract.MovieEntry.TABLE_NAME,
                        // Items of the movie to get
                        projection,
                        // Only get items with the specified ID (only one movie)
                        MovieContract.MovieEntry._ID + " = ? ",
                        // Read the id to filter from the URI
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);

                break;
            }

            // Return all movies of the local database
            case CODE_MOVIE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        // Table we are going to query
                        MovieContract.MovieEntry.TABLE_NAME,
                        // Items of the movie to get
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Users of the delete method will expect the number of rows deleted to be returned.
        int numRowsDeleted;

        // If no selection is given, delete all movie items
        if (selection == null) selection = "1";

        switch (sUriMatcher.match(uri)) {
            // Delete a movie with the specified ID
            case CODE_MOVIE_WITH_ID:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        // Table we are going to query
                        MovieContract.MovieEntry.TABLE_NAME,
                        // Only remove item with the specified ID (only one movie)
                        MovieContract.MovieEntry._ID + " = ? ",
                        // Read the id to filter from the URI
                        new String[] {String.valueOf(ContentUris.parseId(uri))});

                break;

            case CODE_MOVIE:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If we actually deleted any rows, notify that a change has occurred to this URI
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    /**
     * Method to get the type of the provider
     *
     * @param uri - URI to get the type
     * @return type as string
     */
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Getting type not implemented");
    }

    /**
     * Method to insert a movie to the database
     *
     * @param uri - URI for the insert operation
     * @param values - values to insert the movie to the database
     * @return - uri for the inserted movie
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUriWithId(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("Updating movies not implemented");
    }

    /**
     * You do not need to call this method. This is a method specifically to assist the testing
     * framework in running smoothly. You can read more at:
     * http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
     */
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}