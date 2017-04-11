package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

/**
 * RemoteViewsService controlling the data being shown in the scrollable stock detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // Clear the calling identity and store token
                final long identityToken = Binder.clearCallingIdentity();
                Uri stockQuoteUri = Contract.Quote.URI;
                // Get the stock quotes
                data = getContentResolver().query(stockQuoteUri,
                        null,
                        null,
                        null,
                        Contract.Quote.COLUMN_SYMBOL + " ASC");
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
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item_quote);

                // Read data to display widget list items
                String stockSymbol = data.getString(Contract.Quote.POSITION_SYMBOL);
                float stockPrice = data.getFloat(Contract.Quote.POSITION_PRICE);
                float stockPercentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    views.setContentDescription(R.id.symbol, getString(R.string.a11y_name, stockSymbol));
                    views.setContentDescription(R.id.price, getString(R.string.a11y_price, stockPrice));
                    views.setContentDescription(R.id.change, getString(R.string.a11y_change_percentage, stockPercentageChange));
                }
                views.setTextViewText(R.id.symbol, stockSymbol);
                views.setTextViewText(R.id.price, getString(R.string.format_price, stockPrice));
                views.setTextViewText(R.id.change, getString(R.string.format_change_percentage, stockPercentageChange));

                // Set the background color of the change % depending on the value
                if (stockPercentageChange > 0) {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                final Intent fillInIntent = new Intent();

                Uri stockQuoteUri = Contract.Quote.URI;
                fillInIntent.setData(stockQuoteUri);
                views.setOnClickFillInIntent(R.id.widget_list, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(Contract.Quote._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
