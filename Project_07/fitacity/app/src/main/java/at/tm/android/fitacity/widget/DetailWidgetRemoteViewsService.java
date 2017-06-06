package at.tm.android.fitacity.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import at.tm.android.fitacity.ExerciseDetailActivity;
import at.tm.android.fitacity.R;
import at.tm.android.fitacity.data.Exercise;
import at.tm.android.fitacity.data.FitacityContract;

/**
 * RemoteViewsService controlling the data being shown in the scrollable stock detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do here
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // Clear the calling identity and store token
                final long identityToken = Binder.clearCallingIdentity();
                Uri getExerciseUri = FitacityContract.ExerciseEntry.CONTENT_URI;
                // Get the stock quotes
                data = getContentResolver().query(getExerciseUri,
                        null,
                        null,
                        null,
                        null);
                // Restore the calling identity with stored token
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews row = new RemoteViews(getPackageName(), R.layout.list_item);

                // Read data to display widget list items
                int id = data.getInt(data.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_ID));
                String name = data.getString(data.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_NAME));
                String description = data.getString(data.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_DESCRIPTION));
                int category = data.getInt(data.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_CATEGORY));
                String equipment = data.getString(data.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_EQUIPMENT));
                float difficulty = data.getFloat(data.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_DIFFICULTY));
                String videoUrl = data.getString(data.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_VIDEO_URL));
                String imgUrl = data.getString(data.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_IMG_URL));

                row.setTextViewText(R.id.name, name);
                row.setImageViewResource(R.id.avatar, R.drawable.ic_favorite);

                // Add fill in intent to launch the exercise activity when clicking an favorite exercise
                Exercise exercise = new Exercise(id, name, description, category, equipment, difficulty, videoUrl, imgUrl);
                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(ExerciseDetailActivity.EXERCISE_EXTRA_NAME, exercise);

                row.setOnClickFillInIntent(R.id.widget, fillInIntent);

                return row;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(0);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
