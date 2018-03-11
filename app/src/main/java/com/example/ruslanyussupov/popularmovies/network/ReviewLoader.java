package com.example.ruslanyussupov.popularmovies.network;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.ruslanyussupov.popularmovies.model.Review;
import com.example.ruslanyussupov.popularmovies.utils.JsonUtils;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

public class ReviewLoader extends AsyncTaskLoader<List<Review>> {

    private List<Review> mReviews;
    private URL mJsonUrl;

    public ReviewLoader(Context context, URL jsonUrl) {
        super(context);
        mJsonUrl = jsonUrl;
    }

    @Override
    protected void onStartLoading() {
        if (mReviews != null) {
            deliverResult(mReviews);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Review> loadInBackground() {
        String jsonResponse = NetworkUtils.getResponseFromUrl(mJsonUrl);
        return JsonUtils.reviewJsonParser(jsonResponse);
    }

    @Override
    public void deliverResult(List<Review> data) {
        mReviews = data;
        super.deliverResult(data);
    }
}
