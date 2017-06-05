package at.tm.android.fitacity.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import at.tm.android.fitacity.R;
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
                System.out.println("On create");
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // Clear the calling identity and store token
                final long identityToken = Binder.clearCallingIdentity();
                Uri favoriteExerciseQuoteUri = FitacityContract.ExerciseEntry.CONTENT_URI;

                String[] projectionColumns = {
                        FitacityContract.ExerciseEntry.COLUMN_ID,
                        FitacityContract.ExerciseEntry.COLUMN_NAME,
                        FitacityContract.ExerciseEntry.COLUMN_DESCRIPTION,
                        FitacityContract.ExerciseEntry.COLUMN_CATEGORY,
                        FitacityContract.ExerciseEntry.COLUMN_EQUIPMENT,
                        FitacityContract.ExerciseEntry.COLUMN_DIFFICULTY,
                        FitacityContract.ExerciseEntry.COLUMN_VIDEO_URL,
                        FitacityContract.ExerciseEntry.COLUMN_IMG_URL
                };

                // Get the favorite exercises
                data = getContentResolver().query(favoriteExerciseQuoteUri,
                        projectionColumns,
                        null,
                        null,
                        null);

                System.out.println("On data changed");

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
                System.out.println("Get view at with name");

                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item);
                System.out.println("Get view at with name ok");

                // Read data to display widget list items
                int id = data.getInt(0);
                String name = data.getString(1);
                String description = data.getString(2);
                int category = data.getInt(3);
                String equipment = data.getString(4);
                float difficulty = data.getFloat(5);
                String videoUrl = data.getString(6);
                String imgUrl = data.getString(7);

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    views.setContentDescription(R.id.avatar, getString(R.string.a11y_img_url, imgUrl));
                    views.setContentDescription(R.id.tv_exercise_name, getString(R.string.a11y_name, name));
                }*/

                //Glide.with(getApplicationContext()).load(imgUrl).fitCenter().into();
                System.out.println("Set name of exercise to : " + name);
                //views.setTextViewText(R.id.name, name);
                System.out.println("Set name of exercise to : " + name + " finished");
                //views.setImageViewResource(R.id.avatar, R.drawable.ic_dashboard);

                //final Intent fillInIntent = new Intent();

                /*Uri favoriteExerciseUri = FitacityContract.ExerciseEntry.CONTENT_URI;
                fillInIntent.setData(favoriteExerciseUri);
                views.setOnClickFillInIntent(R.id.widget_list, fillInIntent);*/

                /*final AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());

                final int[] widgetIds = manager.getAppWidgetIds(new ComponentName(getApplicationContext(), DetailWidgetProvider.class));

                for (final int widgetId : widgetIds) {
                    manager.updateAppWidget(widgetId, views);
                }*/

                /*final Intent fillInIntent = new Intent();
                Uri exerciseUri = FitacityContract.ExerciseEntry.CONTENT_URI;
                fillInIntent.setData(exerciseUri);
                views.setOnClickFillInIntent(R.id.widget_list, fillInIntent);*/

                return views;
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
                    return data.getInt(0);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
