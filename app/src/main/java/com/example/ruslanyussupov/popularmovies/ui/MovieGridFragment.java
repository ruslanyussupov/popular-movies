package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Context;
import android.content.Intent;
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

public class MovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieAdapter.OnItemClickListener {

    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    // Id for MovieLoader
    private static final int MOVIE_LOADER_ID = 111;

    // Bundle keys for saving instance state
    public static final String BUNDLE_SORT_BY = "sort_by";
    private static final String BUNDLE_MOVIES = "movies";
    private static final String BUNDLE_CURRENT_POSITION = "current_position";

    public static final String EXTRA_MOVIE = "movie";

    private int mSortBy;

    private int mCurrentPosition;

    private boolean mIsTwoPane;

    private MovieAdapter mMovieAdapter;
    private List<Movie> mMovies;

    // Define views for binding
    @BindView(R.id.rv_movies)RecyclerView mMoviesRecyclerView;
    @BindView(R.id.state_tv)TextView mStateTv;
    @BindView(R.id.loading_pb)ProgressBar mLoadingPb;

    public MovieGridFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

        View movieDetailContainer = getActivity().findViewById(R.id.movie_detail_container);
        mIsTwoPane = movieDetailContainer != null
                && movieDetailContainer.getVisibility() == View.VISIBLE;
        Log.d(LOG_TAG, "Is two pane: " + mIsTwoPane);

        // Set up MovieAdapter and GridLayoutManager to RV
        mMovieAdapter = new MovieAdapter(new ArrayList<Movie>(), this);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);
        mMoviesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

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
            mCurrentPosition = savedInstanceState.getInt(BUNDLE_CURRENT_POSITION);
            Log.d(LOG_TAG, "Saved pos state: " + mCurrentPosition);

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
        outState.putInt(BUNDLE_CURRENT_POSITION, mCurrentPosition);
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

    @Override
    public void onClick(int position) {

        mCurrentPosition = position;

        if (mIsTwoPane) {

            showDetailMovieFragment(mCurrentPosition);

        } else {

            Movie movie = mMovies.get(position);
            Intent openDetailActivity = new Intent(getActivity(), DetailActivity.class);
            openDetailActivity.putExtra(EXTRA_MOVIE, movie);
            startActivity(openDetailActivity);

        }

    }

    private void showDetailMovieFragment(int position) {

        Movie movie = mMovies.get(position);

        DetailContentFragment detailContentFragment = new DetailContentFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_MOVIE, movie);
        detailContentFragment.setArguments(args);

        getFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, detailContentFragment)
                .commit();

    }

}
