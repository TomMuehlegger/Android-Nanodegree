package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class DetailActivity extends AppCompatActivity {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_name_textview)
    TextView mNameView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_name_label_textview)
    TextView mNameLabelView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_price_textview)
    TextView mPriceView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_price_label_textview)
    TextView mPriceLabelView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_change_percentage_textview)
    TextView mChangePercentageView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_change_percentage_label_textview)
    TextView mChangePercentageLabelView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_change_absolute_textview)
    TextView mChangeAbsoluteView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.detail_change_absolute_label_textview)
    TextView mChangeAbsoluteLabelView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.stock_history_chart)
    LineChart mChart;

    Bundle mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        String stockSymbol = "";
        float stockPrice = 0;
        float stockPercentageChange = 0;
        float stockAbsoluteChange = 0;
        String stockHistory = "";

        // Restore the saved instance state
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(getString(R.string.stock_data)))) {
            // Set the saved bundle
            mData = savedInstanceState.getBundle(getString(R.string.stock_data));
        } else {
            Intent intent = getIntent();
            mData = intent.getExtras().getBundle(getString(R.string.stock_data));
        }

        if (mData != null) {
            stockSymbol = mData.getString(Contract.Quote.COLUMN_SYMBOL);
            stockPrice = mData.getFloat(Contract.Quote.COLUMN_PRICE);
            stockPercentageChange = mData.getFloat(Contract.Quote.COLUMN_PERCENTAGE_CHANGE);
            stockAbsoluteChange = mData.getFloat(Contract.Quote.COLUMN_ABSOLUTE_CHANGE);
            stockHistory = mData.getString(Contract.Quote.COLUMN_HISTORY);
        }

        mNameView.setText(stockSymbol);
        mNameView.setContentDescription(getString(R.string.a11y_name, mNameView.getText()));
        mNameLabelView.setContentDescription(mNameView.getContentDescription());

        mPriceView.setText(getString(R.string.format_price, stockPrice));
        mPriceView.setContentDescription(getString(R.string.a11y_price, stockPrice));
        mPriceLabelView.setContentDescription(mPriceView.getContentDescription());

        mChangePercentageView.setText(getString(R.string.format_change_percentage, stockPercentageChange));
        mChangePercentageView.setContentDescription(getString(R.string.a11y_change_percentage, stockPercentageChange));
        mChangePercentageLabelView.setContentDescription(mChangePercentageView.getContentDescription());

        mChangeAbsoluteView.setText(getString(R.string.format_change_absolute, stockAbsoluteChange));
        mChangeAbsoluteView.setContentDescription(getString(R.string.a11y_change_absolute, stockAbsoluteChange));
        mChangeAbsoluteLabelView.setContentDescription(mChangeAbsoluteView.getContentDescription());

        Description chartDescription = new Description();

        chartDescription.setTextColor(Color.WHITE);
        chartDescription.setText("Stock: " + stockSymbol);
        chartDescription.setTextSize(12);

        mChart.setDescription(chartDescription);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setViewPortOffsets(20, 20, 20, 20);
        mChart.setBackgroundColor(getColor(R.color.material_grey_800));

        // add data
        setGraphData(stockHistory);
        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Call super.onSaveInstanceState
        super.onSaveInstanceState(outState);
        // Save the stock data in the outState Bundle
        outState.putBundle(getString(R.string.stock_data), mData);
    }

    /**
     * Set the data to the graph
     * @param historyString
     */
    private void setGraphData(String historyString) {

        String[] histories = historyString.split("\n");
        ArrayList<Entry> values = new ArrayList<Entry>();
        float minY = -1;
        float maxY = -1;

        // Set all history entries
        for (String history : histories) {
            String[] historyParts = history.split(",");
            double timeInMillis = Double.parseDouble(historyParts[0]);
            float price = Float.parseFloat(historyParts[1]);

            if ( (minY == -1) && (maxY == -1)) {
                minY = price;
                maxY = price;
            }

            if (price > maxY) {
                maxY = price;
            }

            if (price < minY) {
                minY = price;
            }

            float timeInHours = (float) TimeUnit.MILLISECONDS.toDays((long)timeInMillis);
            values.add(new Entry((float)timeInHours,price));
            Timber.d("Added value: " + (float)timeInHours + " - " + price);
        }

        // Because the history data is delivered from the youngest to oldest data
        Collections.reverse(values);

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.WHITE);
        set1.setValueTextColor(Color.WHITE);
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(true);
        set1.setDrawValues(true);
        set1.setDrawCircleHole(true);
        set1.setCircleColor(Color.WHITE);
        set1.setCircleColorHole(Color.BLACK);
        set1.setHighLightColor(Color.WHITE);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setLabelCount(12);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("MM.YY");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                long millis = TimeUnit.DAYS.toMillis((long) value);
                return mFormat.format(new Date((long)millis));
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(minY - 5);
        leftAxis.setAxisMaximum(maxY + 5);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }
}
