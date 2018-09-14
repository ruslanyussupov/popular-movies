package com.example.ruslanyussupov.popularmovies.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.ruslanyussupov.popularmovies.OnMovieClickListener;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.databinding.ActivityMainBinding;

import static com.example.ruslanyussupov.popularmovies.data.DataSource.*;

public class MainActivity extends AppCompatActivity implements OnMovieClickListener {

    private static final String EXTRA_MOVIE = "movie";

    private boolean mTwoPane;
    private ActivityMainBinding mBinding;
    private MovieGridViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        View tabletLayout = findViewById(R.id.tablet_layout);
        mTwoPane = tabletLayout != null && tabletLayout.getVisibility() == View.VISIBLE;

        // Set our custom Toolbar as ActionBar
        setSupportActionBar(mBinding.toolbar);

        mViewModel = ViewModelProviders.of(this).get(MovieGridViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_grid_container, new MovieGridFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_by, menu);
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        // Set appropriate sorting mode as checked
        switch (mViewModel.getFilter()) {
            case POPULAR:
                menu.findItem(R.id.sort_by_popular).setChecked(true);
                break;
            case TOP_RATED:
                menu.findItem(R.id.sort_by_top_rated).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.sort_by_popular:
                mViewModel.setFilter(Filter.POPULAR);
                item.setChecked(true);
                return true;
            case R.id.sort_by_top_rated:
                mViewModel.setFilter(Filter.TOP_RATED);
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMovieClick(Movie movie) {

        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, DetailContentFragment.create(movie))
                    .commit();
        } else {
            Intent openDetailActivity = new Intent(this, DetailActivity.class);
            openDetailActivity.putExtra(EXTRA_MOVIE, movie);
            startActivity(openDetailActivity);
        }

    }

}
