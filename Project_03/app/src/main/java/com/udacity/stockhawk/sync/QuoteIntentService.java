package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.udacity.stockhawk.R;

import java.util.List;

import timber.log.Timber;

public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        // Load the stocks without historical data
        List<String> stocksNotFound = QuoteSyncJob.getQuotes(getApplicationContext());

        for (final String stockNotFound : stocksNotFound) {
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {
                public void run(){
                    // Display toast that stock not found
                    String message = getString(R.string.toast_stock_added_not_found, stockNotFound);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
