package com.example.android.project_02.utilities;

import android.net.Uri;

import com.example.android.project_02.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * The NetworkUtils class will be used to handle the communication with the image db api
 */
public final class NetworkUtils {

    // URL to the movie database api
    private static final String TMDB_API_URL = "http://api.themoviedb.org/3/movie/";

    private final static String API_KEY_PARAM = "api_key";

    /**
     * Build URL depending on the sortByString
     *
     * @param sortByString - could be popular or
     * @return URL
     */
    public static URL buildUrl(String sortByString) {
        // The movie db api key is stored in the gradle.properties file
        Uri builtUri = Uri.parse(TMDB_API_URL).buildUpon()
                .appendPath(sortByString)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Build movie trailers URL depending on the movie id
     *
     * @param id - id of the movie to get the trailers
     * @return URL
     */
    public static URL buildTrailerUrl(int id) {
        // The movie db api key is stored in the gradle.properties file
        Uri builtUri = Uri.parse(TMDB_API_URL).buildUpon()
                .appendPath(id + "")
                .appendPath("videos")
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Build movie reviews URL depending on the movie id
     *
     * @param id - id of the movie to get the revies
     * @return URL
     */
    public static URL buildReviewUrl(int id) {
        // The movie db api key is stored in the gradle.properties file
        Uri builtUri = Uri.parse(TMDB_API_URL).buildUpon()
                .appendPath(id + "")
                .appendPath("reviews")
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Get response of a HHTP request with specified url
     *
     * @param url The URL to get the HTTP response from.
     * @return content of the HTTP request
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}