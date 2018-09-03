package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ruslanyussupov.popularmovies.BuildConfig;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.adapters.ReviewAdapter;
import com.example.ruslanyussupov.popularmovies.data.model.Review;
import com.example.ruslanyussupov.popularmovies.data.model.ReviewsResponse;
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbAPI;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewFragment extends Fragment implements ReviewAdapter.OnReviewClickListener {

    private static final String LOG_TAG = ReviewFragment.class.getSimpleName();
    private static final String BUNDLE_MOVIE_ID = "movie_id";
    private static final String BUNDLE_REVIEWS = "reviews";

    private int mMovieId;
    private List<Review> mReviews;
    private ReviewAdapter mAdapter;
    private TheMovieDbAPI mTheMovieDbAPI;

    @BindView(R.id.reviews_rv)RecyclerView mReviewsRv;
    @BindView(R.id.state_tv)TextView mStateTv;

    public ReviewFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

        mStateTv.setVisibility(View.GONE);

        mAdapter = new ReviewAdapter(new ArrayList<Review>(), this);
        mReviewsRv.setAdapter(mAdapter);
        mReviewsRv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        int offset = getResources().getDimensionPixelOffset(R.dimen.review_item_offset);
        mReviewsRv.addItemDecoration(new ItemDecoration(0,0, offset, 0));

        if (savedInstanceState == null) {

            mMovieId = getArguments().getInt(BUNDLE_MOVIE_ID);

            if (NetworkUtils.hasNetworkConnection(getActivity())) {

                mTheMovieDbAPI = NetworkUtils.getMovieDbApi();

                mTheMovieDbAPI.getMovieReviews(mMovieId).enqueue(new Callback<ReviewsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ReviewsResponse> call,
                                           @NonNull retrofit2.Response<ReviewsResponse> response) {
                        if (response.isSuccessful()) {
                            ReviewsResponse reviewsResponse = response.body();
                            if (reviewsResponse == null) {
                                showEmptyState();
                            } else {
                                List<Review> reviews = reviewsResponse.getResults();
                                if (reviews == null || reviews.isEmpty()) {
                                    showEmptyState();
                                } else {
                                    showReviews();
                                    mAdapter.updateData(reviews);
                                }
                            }
                        } else {
                            showEmptyState();
                            Log.d(LOG_TAG, "Code: " + response.code() + " Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ReviewsResponse> call, @NonNull Throwable t) {
                        Log.e(LOG_TAG, "Can't load reviews for " + mMovieId, t);
                    }
                });

            } else {
                showNoNetworkConnectionState();
            }

        } else {

            mMovieId = savedInstanceState.getInt(BUNDLE_MOVIE_ID);
            mReviews = savedInstanceState.getParcelableArrayList(BUNDLE_REVIEWS);

            if (mReviews != null) {
                showReviews();
                mAdapter.updateData(mReviews);
            } else {
                showEmptyState();
            }

        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
    public void onReviewClick(int position) {
        Review review = mReviews.get(position);
        Intent openReviewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
        if (openReviewIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(openReviewIntent);
        }
    }

    // Create review fragment with args
    public static ReviewFragment create(int movieId) {
        ReviewFragment reviewFragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_MOVIE_ID, movieId);
        reviewFragment.setArguments(args);
        return reviewFragment;
    }

    private void showNoNetworkConnectionState() {
        mReviewsRv.setVisibility(View.GONE);
        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(R.string.no_network_connection_state);
    }

    private void showEmptyState() {
        mReviewsRv.setVisibility(View.GONE);
        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(R.string.empty_state_reviews);
    }

    private void showReviews() {
        mReviewsRv.setVisibility(View.VISIBLE);
        mStateTv.setVisibility(View.GONE);
    }

}
