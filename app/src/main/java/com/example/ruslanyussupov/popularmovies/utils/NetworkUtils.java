package com.example.ruslanyussupov.popularmovies.utils;


import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.ruslanyussupov.popularmovies.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private static final String DISCOVER_MOVIE_URL = "https://api.themoviedb.org/3/discover/movie";

    private static final String QUERY_KEY_API_KEY = "api_key";
    private static final String QUERY_KEY_LANGUAGE = "language";
    private static final String QUERY_KEY_SORT_BY = "sort_by";
    private static final String QUERY_KEY_INCLUDE_ADULT = "include_adult";
    private static final String QUERY_KEY_INCLUDE_VIDEO = "include_video";
    private static final String QUERY_KEY_PAGE = "page";


    /**
     * Takes URL as String and make connection to read the data and return it.
     *
     * @param url the URL from we want to get data.
     * */
    public static String getResponseFromUrl(URL url) {

        // If the URL is null then return null
        if (url == null) {
            return null;
        }

        String response = null;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            // Create connection through the URL
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            // Get InputStream from the connection
            inputStream = urlConnection.getInputStream();

            // Decode bytes from the InputStream and put them into BufferedReader for the
            // efficient reading
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Create StringBuilder that will contain our data from the BufferedReader
            StringBuilder stringBuilder = new StringBuilder();

            // Read each line from BufferedReader and append them to the StringBuilder
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            response = stringBuilder.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while getting response from URL.", e);

        } finally {

            if (urlConnection !=  null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "InputStream closing error.", e);
                }

            }
        }

        Log.v(LOG_TAG, "JSON response: " + response);

        return response;

    }

    // Takes URL as String and returns URL object
    private static URL makeUrl(String urlString) {

        // If URL is empty then return null
        if (TextUtils.isEmpty(urlString)) {
            return null;
        }

        URL url = null;

        // Create and return URL object from String
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Creating URL error.", e);
        }

        return url;

    }

    public static URL buildUrlDiscoverMovies(Context context) {

        Uri discoverMoviesQueryUri = Uri.parse(DISCOVER_MOVIE_URL).buildUpon()
                .appendQueryParameter(QUERY_KEY_API_KEY, context.getString(R.string.themoviedb_api_key))
                .appendQueryParameter(QUERY_KEY_LANGUAGE, "en-US")
                .appendQueryParameter(QUERY_KEY_SORT_BY, "popularity.desc")
                .appendQueryParameter(QUERY_KEY_INCLUDE_ADULT, "false")
                .appendQueryParameter(QUERY_KEY_INCLUDE_VIDEO, "false")
                .appendQueryParameter(QUERY_KEY_PAGE, "1")
                .build();

        return makeUrl(discoverMoviesQueryUri.toString());

    }

}
