package at.tm.android.fitacity.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the fitacity database. This class is not necessary, but keeps
 * the code organized.
 */
public class FitacityContract {

    // Main package of the content provider
    static final String CONTENT_AUTHORITY = "at.tm.android.fitacity";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider for project_02.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Paths to the fitacity content providers
    private static final String PATH_CATEGORY = "category";
    static final String PATH_EXERCISE = "exercise";

    // Inner class that defines the table contents of the category table
    public static final class CategoryEntry implements BaseColumns {
        // Used internally as the name of our movie table.
        static final String TABLE_NAME = "category";

        // Name of the ID column
        public static final String COLUMN_ID = "id";
        // Name of the NAME column
        public static final String COLUMN_NAME = "name";
        // Name of the DESCRIPTION column
        public static final String COLUMN_DESCRIPTION = "description";
        // Name of the MAIN_CATEGORY column
        public static final String COLUMN_MAIN_CATEGORY = "main_category";
    }

    // Inner class that defines the table contents of the exercise table
    public static final class ExerciseEntry implements BaseColumns {

        // The base CONTENT_URI used to query the exercise table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EXERCISE)
                .build();

        // Used internally as the name of our movie table.
        static final String TABLE_NAME = "exercise";

        // Name of the ID column
        public static final String COLUMN_ID = "id";
        // Name of the NAME column
        public static final String COLUMN_NAME = "name";
        // Name of the DESCRIPTION column
        public static final String COLUMN_DESCRIPTION = "description";
        // Name of the CATEGORY column
        public static final String COLUMN_CATEGORY = "category";
        // Name of the EQUIPMENT column
        public static final String COLUMN_EQUIPMENT = "equipment";
        // Name of the DIFFICULTY column
        public static final String COLUMN_DIFFICULTY = "difficulty";
        // Name of the VIDEO_URL column
        public static final String COLUMN_VIDEO_URL = "video_url";
        // Name of the IMG_URL column
        public static final String COLUMN_IMG_URL = "img_url";

        // for building URIs on insertion
        public static Uri buildExerciseUriWithId(int id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}