package com.example.android.project_01;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.project_01.data.Movie;
import com.example.android.project_01.utilities.MovieJsonUtils;
import com.example.android.project_01.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Main activity representing the movie overview in a grid layout
 */
public class MainActivity extends AppCompatActivity {

    private MovieRecyclerViewAdapter mMovieAdapter;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private RecyclerView mMovieOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the views of the main_activity
        mLoadingIndicator = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mMovieOverview = (RecyclerView)findViewById(R.id.gv_movies_overview);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mMovieAdapter = new MovieRecyclerViewAdapter(new ArrayList<Movie>());

        // Set the adapter of the recycler view
        mMovieOverview.setAdapter(mMovieAdapter);
        // Set the layout manager of the recycler view to a grid layout
        mMovieOverview.setLayoutManager(new GridLayoutManager(mMovieOverview.getContext(), 2));

        // Load the movie data sorted by popularity
        loadMovieData(getString(R.string.sort_by_popular));
    }

    /**
     * This method loads the movie data from the movie database.
     * The movies can either be sorted by popularity or by rating.
     */
    private void loadMovieData(String sortByString) {
        showMovieDataView();

        new FetchMovieDataTask().execute(sortByString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Get the menu inflater handle
        MenuInflater inflater = getMenuInflater();
        // Inflate the sort by menu
        inflater.inflate(R.menu.sort_by_menu, menu);
        // Return true, to display the menu in the toolbar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // If item id is action_sort_by_popular, load movie data sorted by popularity
        if (id == R.id.action_sort_by_popular) {
            loadMovieData(getString(R.string.sort_by_popular));
            return true;
        }
        // Else if item id is action_sort_by_rating, load movie data sorted by rating
        else if (id == R.id.action_sort_by_rating) {
            loadMovieData(getString(R.string.sort_by_rating));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMovieDataView() {
        // Hide the error message
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Show the movie overview
        mMovieOverview.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        // Hide the movie overview
        mMovieOverview.setVisibility(View.INVISIBLE);
        // Show the error message
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMovieDataTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show the loading indicator
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            // Set sort by popular to default sort order
            String sortByString = getString(R.string.sort_by_popular);

            /* If there is no sort by parameter -> use default "popular" */
            if (params.length != 0) {
                sortByString = params[0];
            }

            // Build url to get the movie data
            URL movieDataRequestUrl = NetworkUtils.buildUrl(sortByString);

            try {
                String jsonMovieDataResponse = NetworkUtils.getResponseFromHttpUrl(movieDataRequestUrl);

                // Parse movies from json response string
                return MovieJsonUtils.getMovieDataFromJson(jsonMovieDataResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movieData) {
            // Hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            // If a valid movieData received
            if (movieData != null) {
                // Show the movie data view and update the recycle view adapter
                showMovieDataView();
                mMovieAdapter.updateMovieData(movieData);
            } else {
                // Otherwise show the error message
                showErrorMessage();
            }
        }
    }
}
