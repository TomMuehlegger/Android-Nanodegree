package com.example.android.project_02;

import android.content.ContentResolver;
import android.database.Cursor;
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

import com.example.android.project_02.data.Movie;
import com.example.android.project_02.data.MovieContract;
import com.example.android.project_02.utilities.MovieJsonUtils;
import com.example.android.project_02.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main activity representing the movie overview in a grid layout
 */
public class MainActivity extends AppCompatActivity {

    private static final String MOVIE_LIST_KEY = "movieList";
    private static final String SORT_CRITERIA_KEY = "sortCriteria";

    private MovieRecyclerViewAdapter mMovieAdapter;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private RecyclerView mMovieOverview;
    private String mSortCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the views of the main_activity
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mMovieOverview = (RecyclerView) findViewById(R.id.rv_movies_overview);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mMovieAdapter = new MovieRecyclerViewAdapter(new ArrayList<Movie>());

        // Set the adapter of the recycler view
        mMovieOverview.setAdapter(mMovieAdapter);
        // Set the layout manager of the recycler view to a grid layout
        mMovieOverview.setLayoutManager(new GridLayoutManager(this, 2));

        if ((savedInstanceState != null) && (savedInstanceState.containsKey(MOVIE_LIST_KEY)) && (savedInstanceState.containsKey(SORT_CRITERIA_KEY))) {
            // Set the sort criteria string
            mSortCriteria = savedInstanceState.getString(SORT_CRITERIA_KEY);

            // If there is a previous movie list stored, update the movie list adapter
            ArrayList<Movie> previousMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_KEY);
            mMovieAdapter.updateMovieData(previousMovieList);
        } else {
            // Set the default sort criteria string to sort by popular
            mSortCriteria = getString(R.string.sort_by_popular);
            // otherwise load data from the movie db server
            loadMovieData(mSortCriteria);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Call super.onSaveInstanceState
        super.onSaveInstanceState(outState);
        // Save the movie list in the outState Bundle
        outState.putParcelableArrayList(MOVIE_LIST_KEY, mMovieAdapter.getMovies());
        outState.putString(SORT_CRITERIA_KEY, mSortCriteria);
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
            mSortCriteria = getString(R.string.sort_by_popular);
            loadMovieData(getString(R.string.sort_by_popular));
            return true;
        }
        // Else if item id is action_sort_by_rating, load movie data sorted by rating
        else if (id == R.id.action_sort_by_rating) {
            mSortCriteria = getString(R.string.sort_by_rating);
            loadMovieData(getString(R.string.sort_by_rating));
            return true;
        } else if (id == R.id.action_show_favorites) {
            mSortCriteria = getString(R.string.show_favorites);
            displayMovieData(getFavoriteMovies());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSortCriteria.equals(getString(R.string.show_favorites))) {
            displayMovieData(getFavoriteMovies());
        }
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

    private ArrayList<Movie> getFavoriteMovies() {
        ArrayList<Movie> movies = new ArrayList<>();

        final ContentResolver resolver = getContentResolver();
        String[] projectionColumns = {MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_POSTER_URL,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_USER_RATING
        };

        Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                projectionColumns,
                null,
                null,
                null);

        // Iterate over all favorite movies
        while (cursor.moveToNext()) {
            // Set the toggle button to checked
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String overview = cursor.getString(2);
            String posterUrl = cursor.getString(3);
            Date releaseDate = new Date(cursor.getLong(4));
            double userRating = cursor.getDouble(5);

            Movie movie = new Movie(id, title, posterUrl, overview, userRating, releaseDate);
            movies.add(movie);
        }

        return movies;
    }

    /**
     * Method to display the movie data in the recycler view
     *
     * @param movieData - movie list to display
     */
    private void displayMovieData(ArrayList<Movie> movieData) {
        // If the provided movie data is valid
        if (movieData != null) {
            // Show the movie data view and update the recycle view adapter
            showMovieDataView();
            mMovieAdapter.updateMovieData(movieData);
        } else {
            // Otherwise show the error message
            showErrorMessage();
        }
    }

    public class FetchMovieDataTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show the loading indicator
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

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
        protected void onPostExecute(ArrayList<Movie> movieData) {
            // Hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            // Display the movie data in the recylcer view
            displayMovieData(movieData);
        }
    }

}
