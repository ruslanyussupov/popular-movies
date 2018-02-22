package com.example.ruslanyussupov.popularmovies.ui;

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

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.network.MovieLoader;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID = 111;
    private MovieAdapter mMovieAdapter;
    private LoaderManager mLoaderManager;
    private URL mJsonReq;

    @BindView(R.id.rv_movies)RecyclerView mMoviesRecyclerView;
    @BindView(R.id.drawer)DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)NavigationView mNavView;
    @BindView(R.id.toolbar)Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }


        mMovieAdapter = new MovieAdapter(new ArrayList<Movie>());
        mMoviesRecyclerView.setAdapter(mMovieAdapter);
        mMoviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mLoaderManager = getSupportLoaderManager();

        if (mLoaderManager.getLoader(LOADER_ID) == null) {
            mJsonReq = NetworkUtils.getUrlPopularMovies(this);
            mLoaderManager.initLoader(LOADER_ID, null, this);
        } else {
            mLoaderManager.restartLoader(LOADER_ID, null, this);
        }

        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                mDrawerLayout.closeDrawers();

                int itemId = item.getItemId();

                switch (itemId) {
                    case R.id.nav_popular:
                        mJsonReq = NetworkUtils.getUrlPopularMovies(MainActivity.this);
                        mLoaderManager.restartLoader(LOADER_ID, null, MainActivity.this);
                        return true;
                    case R.id.nav_top_rated:
                        mJsonReq = NetworkUtils.getUrlTopRatedMovies(MainActivity.this);
                        mLoaderManager.restartLoader(LOADER_ID, null, MainActivity.this);
                        return true;
                    default:
                        return true;
                }

            }
        });


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
        return new MovieLoader(this, mJsonReq);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if (data == null || data.size() == 0) {
            return;
        }
        mMovieAdapter.updateData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMovieAdapter.updateData(new ArrayList<Movie>());
    }

}
