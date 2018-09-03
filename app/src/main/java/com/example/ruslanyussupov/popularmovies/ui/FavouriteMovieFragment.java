package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ruslanyussupov.popularmovies.OnMovieClickListener;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.adapters.FavouriteMovieAdapter;
import com.example.ruslanyussupov.popularmovies.databinding.FragmentMovieGridBinding;
import com.example.ruslanyussupov.popularmovies.db.MovieContract;
import com.example.ruslanyussupov.popularmovies.events.AddFavouriteEvent;
import com.example.ruslanyussupov.popularmovies.events.RemoveFavouriteEvent;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.utils.DbUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class FavouriteMovieFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FAV_LOADER = 444;
    private static final int MOVIE_GRID_COLUMNS = 2;

    private FavouriteMovieAdapter mAdapter;
    private OnMovieClickListener mMovieClickListener;
    private List<Movie> mMovies;
    FragmentMovieGridBinding mBinding;

    public FavouriteMovieFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mMovieClickListener = (OnMovieClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "  must implement OnMovieClickListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        LoaderManager loaderManager = getLoaderManager();
        if (loaderManager.getLoader(FAV_LOADER) == null) {
            loaderManager.initLoader(FAV_LOADER, null, this);
        } else {
            loaderManager.restartLoader(FAV_LOADER, null, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_grid,
                container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new FavouriteMovieAdapter(new ArrayList<Movie>(), mMovieClickListener);
        mBinding.rvMovies.setAdapter(mAdapter);
        mBinding.rvMovies.setLayoutManager(new GridLayoutManager(getActivity(), MOVIE_GRID_COLUMNS));
        int offset = getResources().getDimensionPixelOffset(R.dimen.movie_item_offset);
        mBinding.rvMovies.addItemDecoration(new ItemDecoration(offset, offset, offset, offset));

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        mBinding.loadingPb.setVisibility(View.VISIBLE);

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        mBinding.loadingPb.setVisibility(View.GONE);

        if (data == null || data.getCount() == 0) {
            showEmptyState();
            return;
        }

        mMovies = DbUtils.getMoviesFromCursor(data);
        mAdapter.updateData(mMovies);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.updateData(new ArrayList<Movie>());
    }

    private void showEmptyState() {

        mBinding.loadingPb.setVisibility(View.GONE);
        mBinding.stateTv.setVisibility(View.VISIBLE);
        mBinding.stateTv.setText(R.string.empty_state);

    }

    @Subscribe
    public void onFavouriteAdd(AddFavouriteEvent event) {
        mBinding.stateTv.setVisibility(View.GONE);
        mMovies.add(event.getMovie());
        mAdapter.updateData(mMovies);
    }

    @Subscribe
    public void onFavouriteRemoved(RemoveFavouriteEvent event) {
        mMovies.remove(event.getMovie());
        mAdapter.updateData(mMovies);
        if (mMovies.isEmpty()) {
            showEmptyState();
        }
    }

}
