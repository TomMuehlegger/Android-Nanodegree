package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.android.androidjokeslib.DisplayJokeActivity;

public class MainActivity extends AppCompatActivity implements OnJokeTaskCompleted {

    private LinearLayout mLoadingLayout;
    private FrameLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingLayout = (LinearLayout)findViewById(R.id.loading_frame);
        mMainLayout = (FrameLayout)findViewById(R.id.main_frame);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mMainLayout.setVisibility(View.GONE);
        // Retrieve joke from Google Cloud Endpoint
        new EndpointAsyncTask().execute(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Hide the loader layout and show the main layout
        mLoadingLayout.setVisibility(View.GONE);
        mMainLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskCompleted(String joke) {
        // Create the intent to display the display joke activity
        Intent jokeIntent = new Intent(this, DisplayJokeActivity.class);
        // Set the joke to the extra data
        jokeIntent.putExtra(DisplayJokeActivity.EXTRA_JOKE, joke);
        // Show the display joke activity
        this.startActivity(jokeIntent);
    }
}
