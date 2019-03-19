package com.example.ruslanyussupov.popularmovies.browse

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import com.example.ruslanyussupov.popularmovies.BaseActivity

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter
import com.example.ruslanyussupov.popularmovies.data.DataSource
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.databinding.ActivityMainBinding
import com.example.ruslanyussupov.popularmovies.detail.DetailActivity
import com.example.ruslanyussupov.popularmovies.detail.DetailContentFragment

import timber.log.Timber

import com.example.ruslanyussupov.popularmovies.data.DataSource.*

class MainActivity : BaseActivity(), MovieAdapter.OnMovieClickListener {

    private var isTwoPane = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var filter: Filter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val tabletLayout = findViewById<View>(R.id.tablet_layout)
        isTwoPane = tabletLayout != null && tabletLayout.visibility == View.VISIBLE

        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        if (savedInstanceState == null) {
            supportFragmentManager.transaction { add(R.id.movies_grid_container, MovieGridFragment()) }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("onSaveInstanceState")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Timber.d("onCreateOptionsMenu")

        menuInflater.inflate(R.menu.sort_by, menu)

        viewModel.filter.observe(this, Observer {
            filter = it
            when (filter) {
                DataSource.Filter.POPULAR -> { menu.findItem(R.id.sort_by_popular).isChecked = true }
                DataSource.Filter.TOP_RATED -> { menu.findItem(R.id.sort_by_top_rated).isChecked = true }
                DataSource.Filter.FAVOURITE -> { menu.findItem(R.id.sort_by_favourite).isChecked = true }
            }
        })

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val itemId = item?.itemId

        return when (itemId) {
            R.id.sort_by_popular -> {
                viewModel.filterChanged(Filter.POPULAR)
                true
            }
            R.id.sort_by_top_rated -> {
                viewModel.filterChanged(Filter.TOP_RATED)
                true
            }
            R.id.sort_by_favourite -> {
                viewModel.filterChanged(Filter.FAVOURITE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onMovieClick(movie: Movie) {

        if (isTwoPane) {
            supportFragmentManager.transaction {
                replace(R.id.movie_detail_container, DetailContentFragment.create(movie, filter.name))
            }
        } else {
            val openDetailActivity = Intent(this, DetailActivity::class.java)
            openDetailActivity.putExtra(DetailActivity.EXTRA_MOVIE, movie)
            openDetailActivity.putExtra(DetailActivity.EXTRA_FILTER, filter.name)
            startActivity(openDetailActivity)
        }

    }

}
