package com.example.ruslanyussupov.popularmovies.network;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.utils.JsonUtils;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String LOG_TAG = MovieLoader.class.getSimpleName();

    private URL mJsonUrl;

    public MovieLoader(Context context, URL jsonUrl) {
        super(context);
        mJsonUrl = jsonUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {

        String jsonResponse = NetworkUtils.getResponseFromUrl(mJsonUrl);
        return JsonUtils.jsonParser(jsonResponse);

    }

}
