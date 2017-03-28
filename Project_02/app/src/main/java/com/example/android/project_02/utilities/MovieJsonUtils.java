package com.example.android.project_02.utilities;

import com.example.android.project_02.data.Movie;
import com.example.android.project_02.data.Review;
import com.example.android.project_02.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The MovieJsonUtils class will provide methods to get Movie-Objects from JSON
 */
public final class MovieJsonUtils {

    /**
     * Get a movie list from a json string
     *
     * @param movieDataJsonString json string representing movie data
     * @return list of movies
     * @throws JSONException
     * @throws ParseException
     */
    public static ArrayList<Movie> getMovieDataFromJson(String movieDataJsonString) throws JSONException, ParseException {

        final String TMDB_IMAGE_ROOT = "http://image.tmdb.org/t/p/w185/";
        final String MOVIE_LIST = "results";
        final String ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String POSTER_URL = "poster_path";
        final String OVERVIEW = "overview";
        final String USER_RATING = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String RELEASE_DATE_FORMAT = "yyyy-MM-dd";

        JSONObject moviesPageJSON = new JSONObject(movieDataJsonString);
        JSONArray moviesJSON = moviesPageJSON.getJSONArray(MOVIE_LIST);

        ArrayList<Movie> movies = new ArrayList<>();

        // Parse each element of the results array and add it to the movies list
        for (int i = 0; i < moviesJSON.length(); i++) {

            JSONObject movieJSON = moviesJSON.getJSONObject(i);

            int id = movieJSON.getInt(ID);
            String originalTitle = movieJSON.getString(ORIGINAL_TITLE);
            String posterUrl = TMDB_IMAGE_ROOT + movieJSON.getString(POSTER_URL);
            String overview = movieJSON.getString(OVERVIEW);
            double userRating = movieJSON.getDouble(USER_RATING);
            String releaseDateString = movieJSON.getString(RELEASE_DATE);
            Date releaseDate = new SimpleDateFormat(RELEASE_DATE_FORMAT).parse(releaseDateString);

            movies.add(new Movie(id, originalTitle, posterUrl, overview, userRating, releaseDate));
        }

        return movies;

    }

    /**
     * Get a trailer list from a json string
     *
     * @param trailerDataJsonString json string representing trailer data
     * @return list of trailers
     * @throws JSONException
     * @throws ParseException
     */
    public static List<Trailer> getTrailerDataFromJson(String trailerDataJsonString) throws JSONException, ParseException {

        final String TRAILER_LIST = "results";
        final String ID = "id";
        final String NAME = "name";
        final String KEY = "key";

        JSONObject trailersPageJSON = new JSONObject(trailerDataJsonString);
        JSONArray trailersJSON = trailersPageJSON.getJSONArray(TRAILER_LIST);

        List<Trailer> trailers = new ArrayList<>();

        // Parse each element of the results array and add it to the trailers list
        for (int i = 0; i < trailersJSON.length(); i++) {

            JSONObject trailerJSON = trailersJSON.getJSONObject(i);

            String id = trailerJSON.getString(ID);
            String name = trailerJSON.getString(NAME);
            String key = trailerJSON.getString(KEY);

            trailers.add(new Trailer(id, name, key));
        }

        return trailers;

    }

    /**
     * Get a review list from a json string
     *
     * @param reviewDataJsonString json string representing review data
     * @return list of reviews
     * @throws JSONException
     * @throws ParseException
     */
    public static List<Review> getReviewDataFromJson(String reviewDataJsonString) throws JSONException, ParseException {

        final String REVIEW_LIST = "results";
        final String ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";

        JSONObject reviewsPageJSON = new JSONObject(reviewDataJsonString);
        JSONArray reviewsJSON = reviewsPageJSON.getJSONArray(REVIEW_LIST);

        List<Review> reviews = new ArrayList<>();

        // Parse each element of the results array and add it to the reviews list
        for (int i = 0; i < reviewsJSON.length(); i++) {

            JSONObject reviewJSON = reviewsJSON.getJSONObject(i);

            String id = reviewJSON.getString(ID);
            String author = reviewJSON.getString(AUTHOR);
            String content = reviewJSON.getString(CONTENT);

            reviews.add(new Review(id, author, content));
        }

        return reviews;

    }
}