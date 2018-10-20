package com.example.ruslanyussupov.popularmovies.list;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ruslanyussupov.popularmovies.ItemDecoration;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter;
import com.example.ruslanyussupov.popularmovies.databinding.FragmentMovieGridBinding;

import java.util.ArrayList;
import java.util.Collections;

import timber.log.Timber;

import static com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter.*;


public class MovieGridFragment extends Fragment {

    public static final String EXTRA_MOVIE = "movie";

    private static final int MOVIE_GRID_COLUMNS = 2;

    private MovieAdapter mMovieAdapter;
    private OnMovieClickListener mMovieClickListener;
    private FragmentMovieGridBinding mBinding;
    private MainViewModel mViewModel;

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

        Timber.d("onCreateView");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_grid, container, false);
        return mBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.d("onActivityCreated");

        mMovieAdapter = new MovieAdapter(new ArrayList<>(), mMovieClickListener);
        mBinding.rvMovies.setAdapter(mMovieAdapter);
        mBinding.rvMovies.setLayoutManager(new GridLayoutManager(getActivity(), MOVIE_GRID_COLUMNS));
        int offset = getResources().getDimensionPixelOffset(R.dimen.movie_item_offset);
        mBinding.rvMovies.addItemDecoration(new ItemDecoration(offset, offset, offset, offset));

        mViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        mViewModel.getResultLiveData().observe(this, result -> {

            mBinding.rvMovies.scrollToPosition(0);

            switch (result.state) {
                case LOADING:
                    Timber.d("Movies loading...");
                    mBinding.loadingPb.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    mBinding.loadingPb.setVisibility(View.GONE);
                    if (result.data == null || result.data.isEmpty()) {
                        Timber.d("Result is empty.");
                        showSnackBar("No movies!");
                        mMovieAdapter.updateData(Collections.emptyList());
                    } else {
                        Timber.d("Movies loaded successfully: %s", result.data);
                        mMovieAdapter.updateData(result.data);
                    }
                    break;
                case ERROR:
                    mBinding.loadingPb.setVisibility(View.GONE);
                    if (mViewModel.getUtils().hasNetworkConnection()) {
                        Timber.e("Error while loading movies: %s", result.error);
                        showSnackBar(result.error);
                    } else {
                        Timber.w("No network connection.");
                        showSnackBar("No connection!");
                    }
                    break;
            }

        });

    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        super.onDestroy();
    }

    private void showSnackBar(String message) {
        Snackbar.make(mBinding.rvMovies, message, Snackbar.LENGTH_LONG)
                .setAction("Retry", listener -> mViewModel.retry())
                .show();
    }

}
