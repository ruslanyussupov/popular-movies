package com.example.ruslanyussupov.popularmovies.list

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.View

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter
import com.example.ruslanyussupov.popularmovies.data.DataSource
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.databinding.ActivityMainBinding
import com.example.ruslanyussupov.popularmovies.detail.DetailActivity
import com.example.ruslanyussupov.popularmovies.detail.DetailContentFragment
import com.jakewharton.rxbinding2.view.RxMenuItem

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import timber.log.Timber

import com.example.ruslanyussupov.popularmovies.data.DataSource.*

class MainActivity : AppCompatActivity(), MovieAdapter.OnMovieClickListener {

    private var mTwoPane = false
    private var currentFilter = Filter.POPULAR
    private var filterDisposable: Disposable? = null
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val tabletLayout = findViewById<View>(R.id.tablet_layout)
        mTwoPane = tabletLayout != null && tabletLayout.visibility == View.VISIBLE

        setSupportActionBar(mBinding.toolbar)

        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.movies_grid_container, MovieGridFragment())
                    .commit()
        } else {
            currentFilter = Filter.valueOf(savedInstanceState.getString(BUNDLE_FILTER, Filter.FAVOURITE.name))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
        filterDisposable?.dispose()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("onSaveInstanceState")
        outState.putString(BUNDLE_FILTER, currentFilter.name)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Timber.d("onCreateOptionsMenu")

        menuInflater.inflate(R.menu.sort_by, menu)

        val popularMenuItem = menu.findItem(R.id.sort_by_popular)
        val topRatedMenuItem = menu.findItem(R.id.sort_by_top_rated)
        val favouriteMenuItem = menu.findItem(R.id.sort_by_favourite)

        when (currentFilter) {
            DataSource.Filter.POPULAR -> popularMenuItem.isChecked = true
            DataSource.Filter.TOP_RATED -> topRatedMenuItem.isChecked = true
            DataSource.Filter.FAVOURITE -> favouriteMenuItem.isChecked = true
        }

        val popularItemObs = RxMenuItem.clicks(popularMenuItem)
                .map {
                    Timber.d("Filter changed: POPULAR")
                    popularMenuItem.isChecked = true
                    currentFilter = Filter.POPULAR
                    Filter.POPULAR
                }
        val topRatedItemObs = RxMenuItem.clicks(topRatedMenuItem)
                .map {
                    Timber.d("Filter changed: TOP RATED")
                    topRatedMenuItem.isChecked = true
                    currentFilter = Filter.TOP_RATED
                    Filter.TOP_RATED
                }
        val favouriteItemObs = RxMenuItem.clicks(favouriteMenuItem)
                .map {
                    Timber.d("Filter changed: FAVOURITE")
                    favouriteMenuItem.isChecked = true
                    currentFilter = Filter.FAVOURITE
                    Filter.FAVOURITE
                }

        filterDisposable = Observable.merge(Observable.just(currentFilter), popularItemObs,
                topRatedItemObs, favouriteItemObs)
                .subscribe { mViewModel.onFilterChanged(it) }

        return true

    }

    override fun onMovieClick(movie: Movie) {

        if (mTwoPane) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.movie_detail_container, DetailContentFragment.create(movie))
                    .commit()
        } else {
            val openDetailActivity = Intent(this, DetailActivity::class.java)
            openDetailActivity.putExtra(EXTRA_MOVIE, movie)
            startActivity(openDetailActivity)
        }

    }

    companion object {
        private const val EXTRA_MOVIE = "movie"
        private const val BUNDLE_FILTER = "filter"
    }

}
