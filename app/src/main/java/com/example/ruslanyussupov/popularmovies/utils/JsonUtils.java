package com.example.ruslanyussupov.popularmovies.utils;


import android.util.Log;

import com.example.ruslanyussupov.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final String LOG_TAG = JsonUtils.class.getSimpleName();

    // JSON keys
    private static final String RESULTS = "results";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String BACKDROP_PATH = "backdrop_path";


    /**
     * Return List of Movies parsing JSON String
     *
     *@param json JSON String contains movies data
     * */
    public static List<Movie> jsonParser(String json) {

        ArrayList<Movie> movies = null;

        try {

            // Create JSONObject from JSON String
            JSONObject root = new JSONObject(json);

            // Get JSONArray of movies
            JSONArray results = root.getJSONArray(RESULTS);

            int length = results.length();

            // If the JSONArray is empty then return null
            if (length == 0) {
                return null;
            }

            movies = new ArrayList<>(length);

            // Add each movie from the JSONArray to the movies List
            for (int i = 0; i < length; i++) {

                JSONObject currentMovie = (JSONObject) results.get(i);
                String posterPath = currentMovie.getString(POSTER_PATH);
                String originalTitle = currentMovie.getString(ORIGINAL_TITLE);
                double voteAverage = currentMovie.getDouble(VOTE_AVERAGE);
                String overview = currentMovie.getString(OVERVIEW);
                String releaseDate = currentMovie.getString(RELEASE_DATE);
                String backdropPath = currentMovie.getString(BACKDROP_PATH);

                Movie movie = new Movie(originalTitle,
                        posterPath,
                        overview,
                        voteAverage,
                        releaseDate,
                        backdropPath);

                movies.add(movie);

            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Json parse error.", e);
        }

        return movies;
    }

}
