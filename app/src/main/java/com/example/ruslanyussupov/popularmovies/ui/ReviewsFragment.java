package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.adapters.ReviewsAdapter;
import com.example.ruslanyussupov.popularmovies.model.Review;
import com.example.ruslanyussupov.popularmovies.network.ReviewLoader;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Review>>,
        ReviewsAdapter.OnReviewClickListener {

    private static final String LOG_TAG = ReviewsFragment.class.getSimpleName();
    private static final int REVIEWS_LOADER = 333;
    private static final String BUNDLE_MOVIE_ID = "movie_id";
    private static final String BUNDLE_REVIEWS = "reviews";

    private int mMovieId;
    private List<Review> mReviews;
    private ReviewsAdapter mAdapter;

    @BindView(R.id.reviews_rv)RecyclerView mReviewsRv;
    @BindView(R.id.state_tv)TextView mStateTv;

    public ReviewsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

        mStateTv.setVisibility(View.GONE);

        mAdapter = new ReviewsAdapter(new ArrayList<Review>(), this);
        mReviewsRv.setAdapter(mAdapter);
        mReviewsRv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));

        if (savedInstanceState == null) {

            mMovieId = getArguments().getInt(DetailContentFragment.BUNDLE_MOVIE_ID);

            if (hasNetworkConnection()) {

                LoaderManager loaderManager = getLoaderManager();

                if (loaderManager.getLoader(REVIEWS_LOADER) == null) {
                    loaderManager.initLoader(REVIEWS_LOADER, null, this);
                } else {
                    loaderManager.restartLoader(REVIEWS_LOADER, null, this);
                }

            } else {

                showNoNetworkConnectionState();

            }

        } else {

            mMovieId = savedInstanceState.getInt(BUNDLE_MOVIE_ID);
            mReviews = savedInstanceState.getParcelableArrayList(BUNDLE_REVIEWS);

            if (mReviews != null) {
                mStateTv.setVisibility(View.GONE);
                mAdapter.updateData(mReviews);
            } else {
                showEmptyState();
            }

        }




    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_REVIEWS, (ArrayList<? extends Parcelable>) mReviews);
        outState.putInt(BUNDLE_MOVIE_ID, mMovieId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
        return new ReviewLoader(getActivity(), NetworkUtils.getMovieReviewsUrl(mMovieId));
    }

    @Override
    public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
        if (data == null || data.size() == 0) {
            showEmptyState();
            return;
        }

        mStateTv.setVisibility(View.GONE);
        mReviews = data;
        mAdapter.updateData(mReviews);
    }

    @Override
    public void onLoaderReset(Loader<List<Review>> loader) {
        mAdapter.updateData(new ArrayList<Review>());
    }

    @Override
    public void onReviewClick(int position) {
        Review review = mReviews.get(position);
        Intent openReviewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
        if (openReviewIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(openReviewIntent);
        }
    }

    private boolean hasNetworkConnection() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();

        }

        return false;

    }

    private void showNoNetworkConnectionState() {

        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(R.string.no_network_connection_state);

    }

    private void showEmptyState() {

        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(R.string.empty_state_reviews);

    }
}
