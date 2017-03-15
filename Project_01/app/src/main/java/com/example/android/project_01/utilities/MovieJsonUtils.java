package com.example.android.project_01.utilities;

import com.example.android.project_01.data.Movie;

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
    public static List<Movie> getMovieDataFromJson(String movieDataJsonString) throws JSONException, ParseException {

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

        List<Movie> movies = new ArrayList<>();

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
}