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

    private int mSortBy = SORT_BY_POPULAR;

    // Define views for binding
    @BindView(R.id.toolbar)Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        ButterKnife.bind(this);

        setupActionBar();

        MovieGridFragment movieGridFragment = new MovieGridFragment();
        Bundle args = new Bundle();
        args.putInt(MovieGridFragment.BUNDLE_SORT_BY, mSortBy);
        movieGridFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.movies_grid_container, movieGridFragment)
                .commit();

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {

        // Set our custom Toolbar as ActionBar
        setSupportActionBar(mToolbar);

    }

    private void updateMoviesGrid(int sortBy) {

        if (mSortBy == sortBy) {
            return;
        }

        mSortBy = sortBy;

        MovieGridFragment movieGridFragment = new MovieGridFragment();
        Bundle args = new Bundle();
        args.putInt(MovieGridFragment.BUNDLE_SORT_BY, mSortBy);
        movieGridFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movies_grid_container, movieGridFragment).commit();

    }


}
