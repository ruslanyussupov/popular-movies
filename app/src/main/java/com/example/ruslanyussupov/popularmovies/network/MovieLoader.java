package com.example.ruslanyussupov.popularmovies.network;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.utils.JsonUtils;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private URL mJsonUrl;
    private List<Movie> mMovies;

    public MovieLoader(Context context, URL jsonUrl) {
        super(context);
        mJsonUrl = jsonUrl;
    }

    @Override
    protected void onStartLoading() {
        // If there is cached data, then pass it as result, otherwise download it
        if (mMovies != null) {
            deliverResult(mMovies);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Movie> loadInBackground() {
        // Get JSON response from URL and parsing it
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
