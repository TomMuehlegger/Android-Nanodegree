package at.tm.android.fitacity.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * The NetworkUtils class will be used to handle the communication with the fitacity api
 */
public final class NetworkUtils {

    // URL to the fitacity api
    private static final String FITACITY_API_URL = "http://sports-connects.at/fitacity/api/";

    /**
     * Build URL depending
     *
     * @return URL
     */
    public static URL buildUrl() {
        // The movie db api key is stored in the gradle.properties file
        Uri builtUri = Uri.parse(FITACITY_API_URL).buildUpon()
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
    public static URL buildExerciseUrl(int id) {
        Uri builtUri = Uri.parse(FITACITY_API_URL).buildUpon()
                .appendQueryParameter("type", "1")
                .appendQueryParameter("category", Integer.toString(id))
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
     * @throws IOException - On failing input stream
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