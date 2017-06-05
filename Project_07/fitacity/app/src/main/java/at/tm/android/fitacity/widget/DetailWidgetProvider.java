package at.tm.android.fitacity.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Random;

import at.tm.android.fitacity.MainActivity;
import at.tm.android.fitacity.R;
import at.tm.android.fitacity.data.FitacityContract;
import at.tm.android.fitacity.data.FitacityProvider;

/**
 * Provider for a scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //
        // Perform this loop procedure for each App Widget that belongs to this provider
        //for (int appWidgetId : appWidgetIds) {
        System.out.println("On Update");

        for (int i = 0; i < appWidgetIds.length; i++) {
            int widgetId = appWidgetIds[i];

            System.out.println("AppWidget: " + widgetId);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_detail);

            // Create an Intent to launch DetailActivity
            /*Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);*/

            // Set up the collection
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }*/

            //views.setEmptyView(R.id.widget_list, R.id.widget_error);
            /*String number = String.format("%03d", (new Random().nextInt(900) + 100));

            views.setTextViewText(R.id.widget_title, number);

            Intent intent01 = new Intent(context, DetailWidgetProvider.class);
            intent01.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent01.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent01 = PendingIntent.getBroadcast(context,
                    0, intent01, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.action_button, pendingIntent01);*/

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }

            views.setEmptyView(R.id.widget_list, R.id.widget_error);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(widgetId, views);
        }

        /*IntentFilter filter = new IntentFilter();
        filter.addAction(FitacityProvider.ACTION_DATA_UPDATED);
        context.getApplicationContext().registerReceiver(this, filter);

        context.startService(new Intent(context, DetailWidgetRemoteViewsService.class));
        super.onUpdate(context, appWidgetManager, appWidgetIds);*/
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        //if (FitacityProvider.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
            //Toast.makeText(context, "Touched view 01", Toast.LENGTH_SHORT).show();
            System.out.println("On receive 1");
            //context.startService(new Intent(context, DetailWidgetRemoteViewsService.class));
        //}

        //onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, DetailWidgetRemoteViewsService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, DetailWidgetRemoteViewsService.class));
    }
}
