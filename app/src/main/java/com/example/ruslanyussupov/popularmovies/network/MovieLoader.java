package com.example.ruslanyussupov.popularmovies.network;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.utils.JsonUtils;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String LOG_TAG = MovieLoader.class.getSimpleName();

    private URL mJsonUrl;
    private List<Movie> mMovies;

    public MovieLoader(Context context, URL jsonUrl) {
        super(context);
        mJsonUrl = jsonUrl;
    }

    @Override
    protected void onStartLoading() {

        if (mMovies != null) {
            // If there is cached data, then pass it as result
            Log.d(LOG_TAG, "Deliver result");
            deliverResult(mMovies);
        } else {
            // If no cached data, then download it
            forceLoad();
        }
    }

    @Override
    public List<Movie> loadInBackground() {
        Log.d(LOG_TAG, "Loading data");
        String jsonResponse = NetworkUtils.getResponseFromUrl(mJsonUrl);
        return JsonUtils.jsonParser(jsonResponse);

    }

    @Override
    public void deliverResult(List<Movie> data) {
        // Cache data
        mMovies = data;
        super.deliverResult(data);
    }
}
