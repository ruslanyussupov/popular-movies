package com.example.ruslanyussupov.popularmovies.browse


import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter
import com.example.ruslanyussupov.popularmovies.databinding.FragmentMovieGridBinding

import timber.log.Timber

import com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter.*
import com.example.ruslanyussupov.popularmovies.data.DataSource.Filter
import com.example.ruslanyussupov.popularmovies.data.Status


class MovieGridFragment : Fragment() {

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieClickListener: OnMovieClickListener
    private lateinit var binding: FragmentMovieGridBinding
    private lateinit var viewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMovieClickListener) {
            movieClickListener = context
        } else {
            throw ClassCastException("$context must implement OnMovieClickListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Timber.d("onCreateView")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_grid, container, false)
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Timber.d("onActivityCreated")

        movieAdapter = MovieAdapter(movieClickListener::onMovieClick)
        binding.rvMovies.adapter = movieAdapter
        binding.rvMovies.layoutManager = StaggeredGridLayoutManager(MOVIE_GRID_COLUMNS, StaggeredGridLayoutManager.VERTICAL)

        binding.swipeRefresh.isRefreshing = false

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid activity.")

        binding.swipeRefresh.setOnRefreshListener {
            if (viewModel.filter.value == Filter.FAVOURITE) {
                binding.swipeRefresh.isRefreshing = false
            } else {
                viewModel.refresh()
            }
        }

        viewModel.pagedList.observe(this, Observer {pagedList ->
            Timber.d("Paged List: ${pagedList.size}")
            movieAdapter.submitList(pagedList)
        })

        viewModel.networkState.observe(this, Observer {networkState ->
            Timber.d("Network state: ${networkState?.status}")
            when (networkState?.status) {
                Status.RUNNING -> { movieAdapter.isLoading.set(true) }
                Status.SUCCESS -> { movieAdapter.isLoading.set(false) }
                Status.FAILED -> {
                    movieAdapter.isLoading.set(false)
                    showSnackBar(networkState.msg ?: "Failed.")
                }
                else -> { movieAdapter.isLoading.set(false) }
            }
        })

        viewModel.refreshState.observe(this, Observer {refreshState ->
            Timber.d("Refresh state: ${refreshState?.status}")
            when (refreshState?.status) {
                Status.RUNNING -> { binding.swipeRefresh.isRefreshing = true }
                Status.SUCCESS -> { binding.swipeRefresh.isRefreshing = false }
                Status.FAILED -> {
                    binding.swipeRefresh.isRefreshing = false
                    showSnackBar(refreshState.msg ?: "Failed.")
                }
                else -> { binding.swipeRefresh.isRefreshing = false }
            }
        })

    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.rvMovies, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry") { viewModel.retry() }
                .show()
    }

    companion object {
        private const val MOVIE_GRID_COLUMNS = 3
    }

}
