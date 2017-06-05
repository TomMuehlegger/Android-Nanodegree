package at.tm.android.fitacity.data;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;


/**
 * FitacityProvider class extending the ContentProvider to store movies
 * in an SQLite database. Furthermore this content provider could be accessed from
 * any app on the entire phone.
 */
public class FitacityProvider extends ContentProvider {

    /*
     * These constant will be used to match URIs with the data they are looking for. We will take
     * advantage of the UriMatcher class to make that matching MUCH easier than doing something
     * ourselves, such as using regular expressions.
     */
    public static final int CODE_EXERCISE = 100;
    public static final int CODE_EXERCISE_WITH_ID = 101;
    public static final String ACTION_DATA_UPDATED = "at.tm.android.fitacity.ACTION_DATA_UPDATED";

    /*
     * The URI Matcher used by this content provider. s stands for a static member variable
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FitacityDbHelper mOpenHelper;

    /**
     * Method to build a URI matcher to check the type of a URI
     *
     * @return - UriMatcher to check the URI type
     */
    public static UriMatcher buildUriMatcher() {

        // Create a new URI matcher with no_match at default
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FitacityContract.CONTENT_AUTHORITY;

        // This URI is content://url/exercise
        matcher.addURI(authority, FitacityContract.PATH_EXERCISE, CODE_EXERCISE);

        // This URI is content://url/exercise/:exerciseId
        matcher.addURI(authority, FitacityContract.PATH_EXERCISE + "/#", CODE_EXERCISE_WITH_ID);


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
         * lengthy operations will cause lag in your app. Since FitacityDbHelper's constructor is
         * very lightweight, we are safe to perform that initialization here.
         */
        mOpenHelper = new FitacityDbHelper(getContext());
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
        throw new RuntimeException("Inserting multiple data not implemented");
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

            // Return all exercises of the local database
            case CODE_EXERCISE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        // Table we are going to query
                        FitacityContract.ExerciseEntry.TABLE_NAME,
                        // Items of the exerises to get
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            // Return all exercises of the local database
            case CODE_EXERCISE_WITH_ID: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        // Table we are going to query
                        FitacityContract.ExerciseEntry.TABLE_NAME,
                        // Items of the exerises to get
                        projection,
                        // Only get items with the specified ID (only one exercise)
                        FitacityContract.ExerciseEntry.COLUMN_ID + " = ? ",
                        // Read the id to filter from the URI
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
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
            // Delete a exercise with the specified ID
            case CODE_EXERCISE_WITH_ID:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        // Table we are going to query
                        FitacityContract.ExerciseEntry.TABLE_NAME,
                        // Only remove item with the specified ID (only one movie)
                        FitacityContract.ExerciseEntry.COLUMN_ID + " = ? ",
                        // Read the id to filter from the URI
                        new String[] {String.valueOf(ContentUris.parseId(uri))});

                break;

            // Delete all exercises
            case CODE_EXERCISE:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        FitacityContract.ExerciseEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If we actually deleted any rows, notify that a change has occurred to this URI
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

            // Send broadcast to inform widgets,... to update views
            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            getContext().sendBroadcast(dataUpdatedIntent);
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

            case CODE_EXERCISE:
                long id = db.insert(FitacityContract.ExerciseEntry.TABLE_NAME, null, values);

                // insert unless it is already contained in the database
                if (id > 0) {
                    returnUri = FitacityContract.ExerciseEntry.buildExerciseUriWithId((int)id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        // Send broadcast to inform widgets,... to update views
        Intent dataUpdatedIntent = new Intent();
        dataUpdatedIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        getContext().sendBroadcast(dataUpdatedIntent);
        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("Updating exercises not implemented");
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