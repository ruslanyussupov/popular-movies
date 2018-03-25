package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ruslanyussupov.popularmovies.OnMovieClickListener;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.network.MovieLoader;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    // Id for MovieLoader
    private static final int MOVIE_LOADER_ID = 111;

    // Bundle keys for saving instance state
    private static final String BUNDLE_SORT_BY = "sortBy";
    private static final String BUNDLE_MOVIES = "movies";
    public static final String EXTRA_MOVIE = "movie";

    private static final int MOVIE_GRID_COLUMNS = 2;

    private int mSortBy;

    private MovieAdapter mMovieAdapter;
    private List<Movie> mMovies;
    private OnMovieClickListener mMovieClickListener;

    // Define views for binding
    @BindView(R.id.rv_movies)RecyclerView mMoviesRecyclerView;
    @BindView(R.id.state_tv)TextView mStateTv;
    @BindView(R.id.loading_pb)ProgressBar mLoadingPb;

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

        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        // Bind views
        ButterKnife.bind(this, rootView);

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

        mSortBy = getArguments().getInt(BUNDLE_SORT_BY, MainActivity.SORT_BY_POPULAR);

        // Set up MovieAdapter and GridLayoutManager to RV
        mMovieAdapter = new MovieAdapter(new ArrayList<Movie>(), mMovieClickListener);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);
        mMoviesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), MOVIE_GRID_COLUMNS));
        int offset = getResources().getDimensionPixelOffset(R.dimen.movie_item_offset);
        mMoviesRecyclerView.addItemDecoration(new ItemDecoration(offset, offset, offset, offset));

        // If there is nothing to restore start loading, else restore from Bundle
        if (savedInstanceState == null) {

            Log.d(LOG_TAG, "savedInstanceState == null");

            // Check internet connection before loading data
            if (NetworkUtils.hasNetworkConnection(getActivity())) {

                LoaderManager loaderManager = getActivity().getSupportLoaderManager();

                if (loaderManager.getLoader(MOVIE_LOADER_ID) == null) {
                    loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
                } else {
                    loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
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

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        // Show the progress bar
        mLoadingPb.setVisibility(View.VISIBLE);

        URL jsonUrl;

        // Get appropriate URL to fetch movies data
        switch (mSortBy) {
            case MainActivity.SORT_BY_POPULAR:
                jsonUrl = NetworkUtils.getPopularMoviesUrl();
                break;
            case MainActivity.SORT_BY_TOP_RATED:
                jsonUrl = NetworkUtils.getTopRatedMoviesUrl();
                break;
            default:
                throw new IllegalArgumentException("Sort by = " + mSortBy);
        }

        return new MovieLoader(getActivity(), jsonUrl);

    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        // Hide the progress bar
        mLoadingPb.setVisibility(View.GONE);

        // If there is no data show empty state view otherwise hide empty state view
        // and update the adapter with new data
        if (data == null || data.size() == 0) {
            showEmptyState();
            return;
        }

        mStateTv.setVisibility(View.GONE);
        mMovies = data;
        mMovieAdapter.updateData(data);

    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMovieAdapter.updateData(new ArrayList<Movie>());
    }

    public static MovieGridFragment create(int sortBy) {
        MovieGridFragment movieGridFragment = new MovieGridFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_SORT_BY, sortBy);
        movieGridFragment.setArguments(args);
        return movieGridFragment;
    }

    private void showEmptyState() {

        mLoadingPb.setVisibility(View.GONE);
        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(R.string.empty_state);

    }

    private void showNoNetworkConnectionState() {

        mLoadingPb.setVisibility(View.GONE);
        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(R.string.no_network_connection_state);

    }

}
