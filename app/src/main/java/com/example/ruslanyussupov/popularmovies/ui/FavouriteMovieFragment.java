package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.adapters.FavouriteMovieAdapter;
import com.example.ruslanyussupov.popularmovies.db.MovieContract;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.utils.DbUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteMovieFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        FavouriteMovieAdapter.OnFavMovieClickListener,
        DetailContentFragment.OnMovieDeletedListener {

    private static final int FAV_LOADER = 444;
    private static final String EXTRA_MOVIE = "movie";
    private static final int MOVIE_GRID_COLUMNS = 2;

    private FavouriteMovieAdapter mAdapter;
    private List<Movie> mMovies;
    private LoaderManager mLoaderManager;

    private boolean mIsTwoPane;

    // Define views for binding
    @BindView(R.id.rv_movies)RecyclerView mMoviesRv;
    @BindView(R.id.state_tv)TextView mStateTv;
    @BindView(R.id.loading_pb)ProgressBar mLoadingPb;

    public FavouriteMovieFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // If layout contains detail container, then it's two pane mode
        View movieDetailContainer = getActivity().findViewById(R.id.movie_detail_container);
        mIsTwoPane = movieDetailContainer != null
                && movieDetailContainer.getVisibility() == View.VISIBLE;

        mAdapter = new FavouriteMovieAdapter(new ArrayList<Movie>(), this);
        mMoviesRv.setAdapter(mAdapter);
        mMoviesRv.setLayoutManager(new GridLayoutManager(getActivity(), MOVIE_GRID_COLUMNS));
        int offset = getResources().getDimensionPixelOffset(R.dimen.movie_item_offset);
        mMoviesRv.addItemDecoration(new ItemDecoration(offset, offset, offset, offset));

        mLoaderManager = getLoaderManager();
        if (mLoaderManager.getLoader(FAV_LOADER) == null) {
            mLoaderManager.initLoader(FAV_LOADER, null, this);
        } else {
            mLoaderManager.restartLoader(FAV_LOADER, null, this);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mLoaderManager.restartLoader(FAV_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        mLoadingPb.setVisibility(View.VISIBLE);

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mLoadingPb.setVisibility(View.GONE);

        if (data == null || data.getCount() == 0) {
            showEmptyState();
            return;
        }

        mMovies = DbUtils.getMoviesFromCursor(data);
        mAdapter.updateData(mMovies);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.updateData(new ArrayList<Movie>());
    }

    @Override
    public void onFavMovieClick(int position) {

        Movie movie = mMovies.get(position);

        if (mIsTwoPane) {

            DetailContentFragment detailContentFragment = DetailContentFragment.create(movie);
            detailContentFragment.setOnMovieDeletedListener(this);

            getFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailContentFragment)
                    .commit();

        } else {

            Intent openDetailActivity = new Intent(getActivity(), DetailActivity.class);
            openDetailActivity.putExtra(EXTRA_MOVIE, movie);
            startActivity(openDetailActivity);

        }
    }

    private void showEmptyState() {

        mLoadingPb.setVisibility(View.GONE);
        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(R.string.empty_state);

    }

    @Override
    public void onMovieDeleted() {
        mLoaderManager.restartLoader(FAV_LOADER, null, this);
    }
}
