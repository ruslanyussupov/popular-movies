package com.example.ruslanyussupov.popularmovies.ui;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.network.MovieLoader;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID = 111;
    private MovieAdapter mMovieAdapter;

    @BindView(R.id.rv_movies)RecyclerView moviesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mMovieAdapter = new MovieAdapter(new ArrayList<Movie>());
        moviesRecyclerView.setAdapter(mMovieAdapter);
        moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        LoaderManager loaderManager = getSupportLoaderManager();

        if (loaderManager.getLoader(LOADER_ID) == null) {
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(LOADER_ID, null, this);
        }

    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MovieLoader(this, NetworkUtils.buildUrlDiscoverMovies(this));
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        Log.d(LOG_TAG, data.toString());
        mMovieAdapter.updateData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMovieAdapter.updateData(new ArrayList<Movie>());
    }
}
