package at.tm.android.fitacity.data;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

/**
 * Helper for loading a list of exercises.
 */
public class ExerciseLoader extends CursorLoader {
    public static ExerciseLoader newAllExercisesInstance(Context context) {
        return new ExerciseLoader(context, FitacityContract.ExerciseEntry.CONTENT_URI);
    }

    private ExerciseLoader(Context context, Uri uri) {
        super(context, uri, null, null, null, null);
    }

    private interface Query {
        String[] PROJECTION = {
                FitacityContract.ExerciseEntry.COLUMN_ID,
                FitacityContract.ExerciseEntry.COLUMN_NAME,
                FitacityContract.ExerciseEntry.COLUMN_DESCRIPTION,
                FitacityContract.ExerciseEntry.COLUMN_CATEGORY,
                FitacityContract.ExerciseEntry.COLUMN_EQUIPMENT,
                FitacityContract.ExerciseEntry.COLUMN_DIFFICULTY,
                FitacityContract.ExerciseEntry.COLUMN_VIDEO_URL,
                FitacityContract.ExerciseEntry.COLUMN_IMG_URL
        };
    }
}
