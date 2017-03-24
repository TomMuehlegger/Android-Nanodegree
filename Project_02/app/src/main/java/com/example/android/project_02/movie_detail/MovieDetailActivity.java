package com.example.android.project_02.movie_detail;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.project_02.R;
import com.example.android.project_02.data.Movie;
import com.example.android.project_02.data.MovieContract;
import com.example.android.project_02.data.Review;
import com.example.android.project_02.data.Trailer;
import com.example.android.project_02.utilities.MovieJsonUtils;
import com.example.android.project_02.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * MovieDetailActivity class representing a detail view of a movie
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_EXTRA_NAME = "movie";
    final String RELEASE_DATE_FORMAT = "yyyy-MM-dd";

    private TrailerRecyclerViewAdapter mTrailerAdapter;
    private RecyclerView mMovieTrailers;
    private ProgressBar mLoadingTrailersIndicator;
    private TextView mTrailerErrorMessageDisplay;

    private ReviewRecyclerViewAdapter mReviewAdapter;
    private RecyclerView mMovieReviews;
    private ProgressBar mLoadingReviewsIndicator;
    private TextView mReviewErrorMessageDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get movie data from the intent
        Intent intent = getIntent();

        if ( (intent != null) && (intent.hasExtra(MOVIE_EXTRA_NAME)) ) {
            final Movie movie = intent.getParcelableExtra(MOVIE_EXTRA_NAME);

            // Get the different views to display detailed movie data
            TextView movieTitle = (TextView) findViewById(R.id.tv_movie_title);
            ImageView moviePoster = (ImageView) findViewById(R.id.iv_movie_poster);
            TextView movieReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
            TextView movieRating = (TextView) findViewById(R.id.tv_movie_rating);
            ToggleButton markAsFavorite = (ToggleButton) findViewById(R.id.tb_mark_movie_as_favorite);

            final ContentResolver resolver = getContentResolver();
            String[] projectionColumns = {MovieContract.MovieEntry._ID};

            Cursor cursor = resolver.query(MovieContract.MovieEntry.buildMovieUriWithId(movie.getId()),
                    projectionColumns,
                    null,
                    null,
                    null);

            // Set the text off from the mark as favorite toggle button
            markAsFavorite.setTextOn(getString(R.string.remove_from_favorites));
            // Set the text on from the mark as favorite toggle button
            markAsFavorite.setTextOff(getString(R.string.mark_as_favorite));

            // The actual movie is already a favorite movie
            if (cursor.moveToFirst()) {
                // Set the toggle button to checked
                markAsFavorite.setChecked(true);
                // Update text of button to remove the movie from favorites
                markAsFavorite.setText(getString(R.string.remove_from_favorites));
            }
            else {
                // Set the toggle button to unchecked
                markAsFavorite.setChecked(false);
                // Update text of button to mark movie as favorite
                markAsFavorite.setText(getString(R.string.mark_as_favorite));
            }

            markAsFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Add movie to local favorites
                        ContentValues movieValues = new ContentValues();
                        movieValues.put(MovieContract.MovieEntry._ID, movie.getId());
                        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getOriginalTitle());
                        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, movie.getPosterUrl());
                        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate().getTime());
                        movieValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, movie.getUserRating());
                        resolver.insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
                    }
                    else {
                        // Remove movie from local favorites
                        resolver.delete(MovieContract.MovieEntry.buildMovieUriWithId(movie.getId()),
                                null,
                                null);
                    }
                }
            });

            TextView movieOverview = (TextView) findViewById(R.id.tv_movie_overview);

            // Get the global views for the trailers
            mLoadingTrailersIndicator = (ProgressBar)findViewById(R.id.pb_loading_trailers_indicator);
            mMovieTrailers = (RecyclerView)findViewById(R.id.rv_movie_trailers);
            mTrailerErrorMessageDisplay = (TextView) findViewById(R.id.tv_trailer_error_message_display);

            // Get the global views for the reviews
            mLoadingReviewsIndicator = (ProgressBar)findViewById(R.id.pb_loading_reviews_indicator);
            mMovieReviews = (RecyclerView)findViewById(R.id.rv_movie_reviews);
            mReviewErrorMessageDisplay = (TextView) findViewById(R.id.tv_review_error_message_display);

            // Set the movie details to the views
            movieTitle.setText(movie.getOriginalTitle());
            Picasso.with(moviePoster.getContext()).load(movie.getPosterUrl()).into(moviePoster);
            movieReleaseDate.setText(new SimpleDateFormat(RELEASE_DATE_FORMAT).format(movie.getReleaseDate()));
            movieRating.setText(Double.toString(movie.getUserRating()) + " / 10");
            movieOverview.setText(movie.getOverview());

            // Set the trailer adapter
            mTrailerAdapter = new TrailerRecyclerViewAdapter(new ArrayList<Trailer>());
            // Set the adapter of the recycler view
            mMovieTrailers.setAdapter(mTrailerAdapter);
            // Set the layout manager of the recycler view to a linear layout
            mMovieTrailers.setLayoutManager(new LinearLayoutManager(this));

            // Load the movie trailers data
            loadMovieTrailersData(movie.getId());

            // Set the review adapter
            mReviewAdapter = new ReviewRecyclerViewAdapter(new ArrayList<Review>());
            // Set the adapter of the recycler view
            mMovieReviews.setAdapter(mReviewAdapter);
            // Set the layout manager of the recycler view to a linear layout
            mMovieReviews.setLayoutManager(new LinearLayoutManager(this));

            // Load the movie reviews data
            loadMovieReviewsData(movie.getId());
        }
    }

    /**
     * This method loads the movie trailers data from the movie database.
     */
    private void loadMovieTrailersData(int id) {
        showMovieTrailersDataView();

        new MovieDetailActivity.FetchMovieTrailersDataTask().execute(id);
    }

    private void showMovieTrailersDataView() {
        // Hide the error message
        mTrailerErrorMessageDisplay.setVisibility(View.GONE);
        // Show the movie trailers list
        mMovieTrailers.setVisibility(View.VISIBLE);
    }

    private void showTrailerErrorMessage() {
        // Hide the movie overview
        mMovieTrailers.setVisibility(View.GONE);
        // Show the error message
        mTrailerErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method loads the movie reviews data from the movie database.
     */
    private void loadMovieReviewsData(int id) {
        showMovieReviewsDataView();

        new MovieDetailActivity.FetchMovieReviewsDataTask().execute(id);
    }

    private void showMovieReviewsDataView() {
        // Hide the error message
        mReviewErrorMessageDisplay.setVisibility(View.GONE);
        // Show the movie reviews list
        mMovieReviews.setVisibility(View.VISIBLE);
    }

    private void showReviewErrorMessage() {
        // Hide the review overview
        mMovieReviews.setVisibility(View.GONE);
        // Show the error message
        mReviewErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMovieTrailersDataTask extends AsyncTask<Integer, Void, List<Trailer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show the loading indicator
            mLoadingTrailersIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Trailer> doInBackground(Integer... params) {
            // If there is no parameter -> return null
            if (params.length != 0) {
                // Set id of the movie to get the data
                int id = params[0];

                // Build url to get the movie trailers data
                URL movieDataRequestUrl = NetworkUtils.buildTrailerUrl(id);

                try {
                    String jsonMovieTrailersDataResponse = NetworkUtils.getResponseFromHttpUrl(movieDataRequestUrl);

                    // Parse movie trailers from json response string
                    return MovieJsonUtils.getTrailerDataFromJson(jsonMovieTrailersDataResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Trailer> movieTrailersData) {
            // Hide the loading indicator
            mLoadingTrailersIndicator.setVisibility(View.GONE);

            // If valid movieTrailersData received
            if (movieTrailersData != null) {
                // Show the movie trailers data view and update the recycle view adapter
                showMovieTrailersDataView();
                mTrailerAdapter.updateTrailerData(movieTrailersData);
            } else {
                // Otherwise show the error message
                showTrailerErrorMessage();
            }
        }
    }

    public class FetchMovieReviewsDataTask extends AsyncTask<Integer, Void, List<Review>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show the loading indicator
            mLoadingReviewsIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Review> doInBackground(Integer... params) {
            // If there is no parameter -> return null
            if (params.length != 0) {
                // Set id of the movie to get the data
                int id = params[0];

                // Build url to get the movie reviews data
                URL movieDataRequestUrl = NetworkUtils.buildReviewUrl(id);

                try {
                    String jsonMovieReviewsDataResponse = NetworkUtils.getResponseFromHttpUrl(movieDataRequestUrl);

                    // Parse movie reviews from json response string
                    return MovieJsonUtils.getReviewDataFromJson(jsonMovieReviewsDataResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Review> movieReviewsData) {
            // Hide the loading indicator
            mLoadingReviewsIndicator.setVisibility(View.GONE);

            // If valid movieReviewsData received
            if (movieReviewsData != null) {
                // Show the movie reviews data view and update the recycle view adapter
                showMovieReviewsDataView();
                mReviewAdapter.updateReviewData(movieReviewsData);
            } else {
                // Otherwise show the error message
                showReviewErrorMessage();
            }
        }
    }
}
