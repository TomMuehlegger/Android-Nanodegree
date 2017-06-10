package at.tm.android.fitacity.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for fitacity data.
 */
class FitacityDbHelper extends SQLiteOpenHelper {

    // Name of the local SQLite database
    private static final String DATABASE_NAME = "fitacity.db";

    // Increase version when changing the database schema
    private static final int DATABASE_VERSION = 2;

    FitacityDbHelper(Context context) {
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
         * cache our category data.
         */
        final String SQL_CREATE_CATEGORY_TABLE =

                "CREATE TABLE " + FitacityContract.CategoryEntry.TABLE_NAME + " (" +

                // Add the COLUMNS to create
                FitacityContract.CategoryEntry.COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FitacityContract.CategoryEntry.COLUMN_NAME          + " TEXT NOT NULL," +
                FitacityContract.CategoryEntry.COLUMN_DESCRIPTION   + " TEXT," +
                FitacityContract.CategoryEntry.COLUMN_MAIN_CATEGORY + " INTEGER);";

        final String SQL_CREATE_EXERCISE_TABLE =

                "CREATE TABLE " + FitacityContract.ExerciseEntry.TABLE_NAME + " (" +

                // Add the COLUMNS to create
                FitacityContract.ExerciseEntry.COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FitacityContract.ExerciseEntry.COLUMN_NAME          + " TEXT NOT NULL," +
                FitacityContract.ExerciseEntry.COLUMN_DESCRIPTION   + " TEXT," +
                FitacityContract.ExerciseEntry.COLUMN_CATEGORY      + " INTEGER NOT NULL," +
                FitacityContract.ExerciseEntry.COLUMN_EQUIPMENT     + " TEXT," +
                FitacityContract.ExerciseEntry.COLUMN_DIFFICULTY    + " INTEGER," +
                FitacityContract.ExerciseEntry.COLUMN_VIDEO_URL     + " TEXT," +
                FitacityContract.ExerciseEntry.COLUMN_IMG_URL       + " TEXT);";

        // Create the category table
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
        // Create the exercise table
        sqLiteDatabase.execSQL(SQL_CREATE_EXERCISE_TABLE);
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FitacityContract.CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FitacityContract.ExerciseEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}