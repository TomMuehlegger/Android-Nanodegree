package com.example.android.project_01;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.project_01.data.Movie;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

/**
 * MovieDetailActivity class representing a detail view of a movie
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_EXTRA_NAME = "movie";
    final String RELEASE_DATE_FORMAT = "yyyy-MM-dd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get movie data from the intent
        Intent intent = getIntent();
        final Movie movie = intent.getParcelableExtra(MOVIE_EXTRA_NAME);

        // Get the different views to display detailed movie data
        TextView movieTitle = (TextView) findViewById(R.id.tv_movie_title);
        ImageView moviePoster = (ImageView) findViewById(R.id.iv_movie_poster);
        TextView movieReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        TextView movieRating = (TextView) findViewById(R.id.tv_movie_rating);
        TextView movieOverview = (TextView) findViewById(R.id.tv_movie_overview);

        // Set the movie details to the views
        movieTitle.setText(movie.getOriginalTitle());
        Picasso.with(moviePoster.getContext()).load(movie.getPosterUrl()).into(moviePoster);
        movieReleaseDate.setText(new SimpleDateFormat(RELEASE_DATE_FORMAT).format(movie.getReleaseDate()));
        movieRating.setText(Double.toString(movie.getUserRating()) + " / 10");
        movieOverview.setText(movie.getOverview());
    }
}
