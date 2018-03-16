package com.example.ruslanyussupov.popularmovies.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ruslanyussupov.popularmovies.R;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final int SORT_BY_POPULAR = 1;
    public static final int SORT_BY_TOP_RATED = 2;
    public static final int SORT_BY_TOP_FAVOURITE = 3;

    private static final String BUNDLE_SORT_BY = "sort_by";

    private int mSortBy = SORT_BY_POPULAR;

    private static final int ADD_FRAGMENT = 0;
    private static final int REPLACE_FRAGMENT = 1;

    // Define views for binding
    @BindView(R.id.toolbar)Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        ButterKnife.bind(this);

        // Set our custom Toolbar as ActionBar
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {

            showMoviesGrid(mSortBy, ADD_FRAGMENT);

        } else {

            mSortBy = savedInstanceState.getInt(BUNDLE_SORT_BY);

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(BUNDLE_SORT_BY, mSortBy);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_by, menu);
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Set appropriate sorting mode as checked
        switch (mSortBy) {

            case SORT_BY_POPULAR:
                menu.findItem(R.id.sort_by_popular).setChecked(true);
                return true;

            case SORT_BY_TOP_RATED:
                menu.findItem(R.id.sort_by_top_rated).setChecked(true);
                return true;

            case SORT_BY_TOP_FAVOURITE:
                menu.findItem(R.id.sort_by_favourite).setChecked(true);
                return true;

            default:
                throw new IllegalArgumentException("Sort by = " + mSortBy);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.sort_by_popular:
                updateMoviesGrid(SORT_BY_POPULAR);
                item.setChecked(true);
                return true;
            case R.id.sort_by_top_rated:
                updateMoviesGrid(SORT_BY_TOP_RATED);
                item.setChecked(true);
                return true;
            case R.id.sort_by_favourite:
                updateMoviesGrid(SORT_BY_TOP_FAVOURITE);
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateMoviesGrid(int sortBy) {

        if (mSortBy == sortBy) {
            return;
        }

        mSortBy = sortBy;

        showMoviesGrid(mSortBy, REPLACE_FRAGMENT);

    }

    private void showMoviesGrid(int sortBy, int action) {

        MovieGridFragment movieGridFragment = new MovieGridFragment();
        Bundle args = new Bundle();
        args.putInt(MovieGridFragment.BUNDLE_SORT_BY, sortBy);
        movieGridFragment.setArguments(args);

        switch (action) {

            case ADD_FRAGMENT:
                if (sortBy == SORT_BY_TOP_FAVOURITE) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.movies_grid_container, new FavouriteMovieFragment())
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.movies_grid_container, movieGridFragment)
                            .commit();
                }
                break;
            case REPLACE_FRAGMENT:
                if (sortBy == SORT_BY_TOP_FAVOURITE) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movies_grid_container, new FavouriteMovieFragment())
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movies_grid_container, movieGridFragment)
                            .commit();
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported action: " + action);

        }

    }


}
