package com.example.ruslanyussupov.popularmovies.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import com.example.ruslanyussupov.popularmovies.databinding.FragmentMovieGridBinding;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;


public class MovieGridFragment extends Fragment {

    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    // Bundle keys for saving instance state
    public static final String EXTRA_MOVIE = "movie";

    private static final int MOVIE_GRID_COLUMNS = 2;

    private MovieAdapter mMovieAdapter;
    private OnMovieClickListener mMovieClickListener;
    private FragmentMovieGridBinding mBinding;
    private MovieGridViewModel mViewModel;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_grid, container, false);

        return mBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

        // Set up MovieAdapter and GridLayoutManager to RV
        mMovieAdapter = new MovieAdapter(new ArrayList<Movie>(), mMovieClickListener);
        mBinding.rvMovies.setAdapter(mMovieAdapter);
        mBinding.rvMovies.setLayoutManager(new GridLayoutManager(getActivity(), MOVIE_GRID_COLUMNS));
        int offset = getResources().getDimensionPixelOffset(R.dimen.movie_item_offset);
        mBinding.rvMovies.addItemDecoration(new ItemDecoration(offset, offset, offset, offset));

        mViewModel = ViewModelProviders.of(getActivity()).get(MovieGridViewModel.class);

        if (NetworkUtils.hasNetworkConnection(getActivity())) {
            mViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    if (movies == null || movies.isEmpty()) {
                        showEmptyState();
                    } else {
                        showMovies();
                        mMovieAdapter.updateData(movies);
                    }
                }
            });
        } else {
            showNoNetworkConnectionState();
        }


    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
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

}
