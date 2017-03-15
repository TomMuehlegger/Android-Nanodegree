package com.example.android.project_01.utilities;

import android.net.Uri;

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

    // TODO: Replace with your own API key
    private static final String TMDB_API_KEY = "";
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
        Uri builtUri = Uri.parse(TMDB_API_URL).buildUpon()
                .appendPath(sortByString)
                .appendQueryParameter(API_KEY_PARAM, TMDB_API_KEY)
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