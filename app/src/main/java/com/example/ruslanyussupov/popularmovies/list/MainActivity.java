package com.example.ruslanyussupov.popularmovies.list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.databinding.ActivityMainBinding;
import com.example.ruslanyussupov.popularmovies.detail.DetailActivity;
import com.example.ruslanyussupov.popularmovies.detail.DetailContentFragment;
import com.jakewharton.rxbinding2.view.RxMenuItem;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static com.example.ruslanyussupov.popularmovies.data.DataSource.*;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickListener {

    private static final String EXTRA_MOVIE = "movie";
    private static final String BUNDLE_FILTER = "filter";

    private boolean mTwoPane;
    private ActivityMainBinding mBinding;
    private MainViewModel mViewModel;
    private Filter currentFilter;
    private Disposable filterDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        View tabletLayout = findViewById(R.id.tablet_layout);
        mTwoPane = tabletLayout != null && tabletLayout.getVisibility() == View.VISIBLE;

        setSupportActionBar(mBinding.toolbar);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_grid_container, new MovieGridFragment())
                    .commit();
            currentFilter = Filter.POPULAR;
        } else {
            currentFilter = (Filter) savedInstanceState.getSerializable(BUNDLE_FILTER);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy");
        filterDisposable.dispose();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.d("onSaveInstanceState");
        outState.putSerializable(BUNDLE_FILTER, currentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Timber.d("onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_by, menu);

        MenuItem popularMenuItem = menu.findItem(R.id.sort_by_popular);
        MenuItem topRatedMenuItem = menu.findItem(R.id.sort_by_top_rated);
        MenuItem favouriteMenuItem = menu.findItem(R.id.sort_by_favourite);

        switch (currentFilter) {
            case POPULAR:
                popularMenuItem.setChecked(true);
                break;
            case TOP_RATED:
                topRatedMenuItem.setChecked(true);
                break;
            case FAVOURITE:
                favouriteMenuItem.setChecked(true);
                break;
        }

        Observable<Filter> popularItemObs =
                RxMenuItem.clicks(popularMenuItem)
                .map(ignore -> {
                    Timber.d("Filter changed: POPULAR");
                    popularMenuItem.setChecked(true);
                    return currentFilter = Filter.POPULAR;
                });
        Observable<Filter> topRatedItemObs =
                RxMenuItem.clicks(topRatedMenuItem)
                .map(ignore -> {
                    Timber.d("Filter changed: TOP RATED");
                    topRatedMenuItem.setChecked(true);
                    return currentFilter = Filter.TOP_RATED;
                });
        Observable<Filter> favouriteItemObs =
                RxMenuItem.clicks(favouriteMenuItem)
                .map(ignore -> {
                    Timber.d("Filter changed: FAVOURITE");
                    favouriteMenuItem.setChecked(true);
                    return currentFilter = Filter.FAVOURITE;
                });

        filterDisposable = Observable.merge(Observable.just(currentFilter), popularItemObs,
                topRatedItemObs, favouriteItemObs)
                .subscribe(mViewModel::onFilterChanged);

        return true;

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
