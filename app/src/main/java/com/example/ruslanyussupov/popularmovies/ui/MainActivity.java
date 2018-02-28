package com.example.ruslanyussupov.popularmovies.ui;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.network.MovieLoader;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>,
        MovieAdapter.OnItemClickListener {

    // Id for MovieLoader
    private static final int MOVIE_LOADER_ID = 111;

    // Bundle keys for saving instance state
    private static final String BUNDLE_SORT_BY = "sort_by";
    private static final String BUNDLE_MOVIES = "movies";

    public static final String EXTRA_MOVIE = "movie";

    private static final int SORT_BY_POPULAR = 1;
    private static final int SORT_BY_TOP_RATED = 2;

    private MovieAdapter mMovieAdapter;
    private LoaderManager mLoaderManager;
    private int mSortBy = SORT_BY_POPULAR;
    private List<Movie> mMovies;

    // Define views for binding
    @BindView(R.id.rv_movies)RecyclerView mMoviesRecyclerView;
    @BindView(R.id.drawer)DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)NavigationView mNavView;
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.state_tv)TextView mStateTv;
    @BindView(R.id.loading_pb)ProgressBar mLoadingPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        ButterKnife.bind(this);

        setupActionBar();

        mLoaderManager = getSupportLoaderManager();

        // Set up MovieAdapter and GridLayoutManager to RV
        mMovieAdapter = new MovieAdapter(new ArrayList<Movie>(), this);
        mMoviesRecyclerView.setAdapter(mMovieAdapter);
        mMoviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // If there is nothing to restore start loading, else restore from Bundle
        if (savedInstanceState == null) {

            // Check internet connection before loading data
            if (hasNetworkConnection()) {

                if (mLoaderManager.getLoader(MOVIE_LOADER_ID) == null) {
                    mLoaderManager.initLoader(MOVIE_LOADER_ID, null, this);
                } else {
                    mLoaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
                }

            } else {

                showNoNetworkConnectionState();

            }


        } else {
            mSortBy = savedInstanceState.getInt(BUNDLE_SORT_BY);
            mMovies = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES);

            if (mMovies == null || mMovies.isEmpty()) {
                if (!hasNetworkConnection())  {
                    showNoNetworkConnectionState();
                } else {
                    showEmptyState();
                }
            } else {
                mMovieAdapter.updateData(mMovies);
            }
        }

        setupNavDrawer();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(BUNDLE_SORT_BY, mSortBy);
        outState.putParcelableArrayList(BUNDLE_MOVIES, (ArrayList<Movie>) mMovies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {

        // Show the progress bar
        mLoadingPb.setVisibility(View.VISIBLE);

        URL jsonUrl;

        // Get appropriate URL to fetch movies data
        switch (mSortBy) {
            case SORT_BY_POPULAR:
                jsonUrl = NetworkUtils.getPopularMoviesUrl();
                break;
            case SORT_BY_TOP_RATED:
                jsonUrl = NetworkUtils.getTopRatedMoviesUrl();
                break;
            default:
                throw new IllegalArgumentException("Sort by = " + mSortBy);
        }

        return new MovieLoader(this, jsonUrl);
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

    @Override
    public void onClick(int position) {
        Movie movie = mMovies.get(position);
        Intent openDetailActivity = new Intent(MainActivity.this, DetailActivity.class);
        openDetailActivity.putExtra(EXTRA_MOVIE, movie);
        startActivity(openDetailActivity);
    }

    private void setupNavDrawer() {

        // Set appropriate sorting mode as checked in the navigation drawer
        switch (mSortBy) {

            case SORT_BY_POPULAR:
                mNavView.getMenu().getItem(0).setChecked(true);
                break;

            case SORT_BY_TOP_RATED:
                mNavView.getMenu().getItem(1).setChecked(true);
                break;

            default:
                throw new IllegalArgumentException("Sort by = " + mSortBy);

        }

        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Close drawer if item is clicked
                mDrawerLayout.closeDrawers();

                int itemId = item.getItemId();

                switch (itemId) {

                    case R.id.nav_popular:
                        updateUi(SORT_BY_POPULAR);
                        return true;

                    case R.id.nav_top_rated:
                        updateUi(SORT_BY_TOP_RATED);
                        return true;

                    default:
                        return false;
                }

            }
        });

    }

    private void updateUi(int sortBy) {

        // If sorting is same, then no need to update UI
        if (mSortBy == sortBy) {
            return;
        }

        mSortBy = sortBy;

        // If there is network connection load data
        // Otherwise clear adapter's data and show no network connection state
        if (hasNetworkConnection()) {
            mLoaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
        } else {
            mMovieAdapter.updateData(new ArrayList<Movie>());
            showNoNetworkConnectionState();
        }

    }

    private void setupActionBar() {

        // Set our custom Toolbar as ActionBar
        setSupportActionBar(mToolbar);

        // Get ActionBar
        ActionBar actionBar = getSupportActionBar();

        // Set icon to open the navigation drawer
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

    }

    private boolean hasNetworkConnection() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();

        }

        return false;

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
