package com.example.ruslanyussupov.popularmovies.utils;


import android.util.Log;

import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.data.model.Review;
import com.example.ruslanyussupov.popularmovies.data.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final String LOG_TAG = JsonUtils.class.getSimpleName();

    // JSON keys
    private static final String ID = "id";
    private static final String RESULTS = "results";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String BACKDROP_PATH = "backdrop_path";
    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String SITE = "site";
    private static final String SIZE = "size";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String REVIEW_URL = "url";

    /**
     * Return List of Movies parsing JSON String
     *
     *@param json JSON String contains movies data
     * */
    public static List<Movie> moviesJsonParser(String json) {

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

                JSONObject currentMovie = results.getJSONObject(i);
                int id = currentMovie.getInt(ID);
                String posterPath = currentMovie.getString(POSTER_PATH);
                String originalTitle = currentMovie.getString(ORIGINAL_TITLE);
                double voteAverage = currentMovie.getDouble(VOTE_AVERAGE);
                String overview = currentMovie.getString(OVERVIEW);
                String releaseDate = currentMovie.getString(RELEASE_DATE);
                String backdropPath = currentMovie.getString(BACKDROP_PATH);

                Movie movie = new Movie(id,
                        originalTitle,
                        posterPath,
                        overview,
                        voteAverage,
                        releaseDate,
                        backdropPath);

                movies.add(movie);

            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Movies json parse error.", e);
        }

        return movies;
    }

    public static List<Video> videoJsonParser(String json) {

        ArrayList<Video> videos = null;

        try {

            JSONObject root = new JSONObject(json);

            JSONArray results = root.getJSONArray(RESULTS);

            int length = results.length();

            if (length == 0) {
                return null;
            }

            videos = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {

                JSONObject currentVideo = results.getJSONObject(i);

                String key = currentVideo.getString(KEY);
                String name = currentVideo.getString(NAME);
                String site = currentVideo.getString(SITE);
                int size = currentVideo.getInt(SIZE);

                Video video = new Video(key, name, site, size);

                videos.add(video);

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Videos json parse error.", e);
        }

        return videos;

    }

    public static List<Review> reviewJsonParser(String json) {

        ArrayList<Review> reviews = null;

        try {

            JSONObject root = new JSONObject(json);

            JSONArray results = root.getJSONArray(RESULTS);

            int length = results.length();

            if (length == 0) {
                return null;
            }

            reviews = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {

                JSONObject currentReview = results.getJSONObject(i);

                String author = currentReview.getString(AUTHOR);
                String content = currentReview.getString(CONTENT);
                String url = currentReview.getString(REVIEW_URL);

                Review review = new Review(author, content, url);

                reviews.add(review);

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Reviews json parse error.", e);
        }

        return reviews;

    }


}
