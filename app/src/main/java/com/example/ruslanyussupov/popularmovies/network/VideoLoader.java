package com.example.ruslanyussupov.popularmovies.network;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.ruslanyussupov.popularmovies.model.Video;
import com.example.ruslanyussupov.popularmovies.utils.JsonUtils;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

public class VideoLoader extends AsyncTaskLoader<List<Video>> {

    private final URL mJsonUrl;
    private List<Video> mVideos;

    public VideoLoader(Context context, URL jsonUrl) {
        super(context);
        mJsonUrl = jsonUrl;
    }

    @Override
    protected void onStartLoading() {
        if (mVideos != null) {
            deliverResult(mVideos);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Video> loadInBackground() {
        String jsonResponse = NetworkUtils.getResponseFromUrl(mJsonUrl);
        return JsonUtils.videoJsonParser(jsonResponse);
    }

    @Override
    public void deliverResult(List<Video> data) {
        mVideos = data;
        super.deliverResult(data);
    }
}
