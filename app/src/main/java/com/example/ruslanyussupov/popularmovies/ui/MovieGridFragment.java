package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ruslanyussupov.popularmovies.OnMovieClickListener;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.data.model.MoviesResponse;
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbAPI;
import com.example.ruslanyussupov.popularmovies.databinding.FragmentMovieGridBinding;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieGridFragment extends Fragment {

    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    // Bundle keys for saving instance state
    private static final String BUNDLE_SORT_BY = "sortBy";
    private static final String BUNDLE_MOVIES = "movies";
    public static final String EXTRA_MOVIE = "movie";

    private static final int MOVIE_GRID_COLUMNS = 2;

    private int mSortBy;
    private TheMovieDbAPI mTheMovieDbAPI;
    private MovieAdapter mMovieAdapter;
    private List<Movie> mMovies;
    private OnMovieClickListener mMovieClickListener;
    private FragmentMovieGridBinding mBinding;

    public MovieGridFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mMovieClickListener = (OnMovieClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMovieClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_grid, container, false);

        return mBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

        mSortBy = getArguments().getInt(BUNDLE_SORT_BY, MainActivity.SORT_BY_POPULAR);

        // Set up MovieAdapter and GridLayoutManager to RV
        mMovieAdapter = new MovieAdapter(new ArrayList<Movie>(), mMovieClickListener);
        mBinding.rvMovies.setAdapter(mMovieAdapter);
        mBinding.rvMovies.setLayoutManager(new GridLayoutManager(getActivity(), MOVIE_GRID_COLUMNS));
        int offset = getResources().getDimensionPixelOffset(R.dimen.movie_item_offset);
        mBinding.rvMovies.addItemDecoration(new ItemDecoration(offset, offset, offset, offset));

        // If there is nothing to restore start loading, else restore from Bundle
        if (savedInstanceState == null) {

            Log.d(LOG_TAG, "savedInstanceState == null");

            mTheMovieDbAPI = NetworkUtils.getMovieDbApi();

            // Check internet connection before loading data
            if (NetworkUtils.hasNetworkConnection(getActivity())) {

                MoviesCallback callback = new MoviesCallback();

                if (mSortBy == MainActivity.SORT_BY_TOP_RATED) {
                    mTheMovieDbAPI.getTopRatedMovies().enqueue(callback);
                } else {
                    mTheMovieDbAPI.getPopularMovies().enqueue(callback);
                }

            } else {

                showNoNetworkConnectionState();

            }

        } else {

            Log.d(LOG_TAG, "savedInstanceState != null");

            mSortBy = savedInstanceState.getInt(BUNDLE_SORT_BY);
            mMovies = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES);

            if (mMovies == null || mMovies.isEmpty()) {

                if (!NetworkUtils.hasNetworkConnection(getActivity()))  {
                    showNoNetworkConnectionState();
                } else {
                    showEmptyState();
                }

            } else {
                showMovies();
                mMovieAdapter.updateData(mMovies);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");
        outState.putInt(BUNDLE_SORT_BY, mSortBy);
        outState.putParcelableArrayList(BUNDLE_MOVIES, (ArrayList<Movie>) mMovies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    public static MovieGridFragment create(int sortBy) {
        MovieGridFragment movieGridFragment = new MovieGridFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_SORT_BY, sortBy);
        movieGridFragment.setArguments(args);
        return movieGridFragment;
    }

    private void showEmptyState() {
        mBinding.loadingPb.setVisibility(View.GONE);
        mBinding.rvMovies.setVisibility(View.GONE);
        mBinding.stateTv.setVisibility(View.VISIBLE);
        mBinding.stateTv.setText(R.string.empty_state);
    }

    private void showMovies() {
        mBinding.loadingPb.setVisibility(View.GONE);
        mBinding.stateTv.setVisibility(View.GONE);
        mBinding.rvMovies.setVisibility(View.VISIBLE);
    }

    private void showNoNetworkConnectionState() {
        mBinding.loadingPb.setVisibility(View.GONE);
        mBinding.rvMovies.setVisibility(View.GONE);
        mBinding.stateTv.setVisibility(View.VISIBLE);
        mBinding.stateTv.setText(R.string.no_network_connection_state);
    }

    private class MoviesCallback implements Callback<MoviesResponse> {

        @Override
        public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
            if (response.isSuccessful()) {
                MoviesResponse moviesResponse = response.body();
                if (moviesResponse == null) {
                    showEmptyState();
                } else {
                    List<Movie> movies = moviesResponse.getResults();
                    if (movies == null || movies.isEmpty()) {
                        showEmptyState();
                    } else {
                        showMovies();
                        mMovieAdapter.updateData(movies);
                    }
                }
            } else {
                showEmptyState();
                Log.d(LOG_TAG, "Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
            showEmptyState();
            Log.e(LOG_TAG, "Can't fetch popular movies.", t);
        }
    }

}
