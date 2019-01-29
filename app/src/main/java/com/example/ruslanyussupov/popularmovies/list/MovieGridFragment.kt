package com.example.ruslanyussupov.popularmovies.list


import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.example.ruslanyussupov.popularmovies.ItemDecoration
import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter
import com.example.ruslanyussupov.popularmovies.databinding.FragmentMovieGridBinding

import timber.log.Timber

import com.example.ruslanyussupov.popularmovies.adapters.MovieAdapter.*
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.Result


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

        movieAdapter = MovieAdapter(emptyList(), movieClickListener::onMovieClick)
        binding.rvMovies.adapter = movieAdapter
        binding.rvMovies.layoutManager = GridLayoutManager(activity, MOVIE_GRID_COLUMNS)
        val offset = resources.getDimensionPixelOffset(R.dimen.movie_item_offset)
        binding.rvMovies.addItemDecoration(ItemDecoration(offset, offset, offset, offset))

        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        viewModel.getResultLiveData().observe(this, Observer<Result<List<Movie>>> { result ->

            binding.rvMovies.scrollToPosition(0)

            when (result.state) {
                Result.State.LOADING -> {
                    Timber.d("Movies loading...")
                    binding.loadingPb.visibility = View.VISIBLE
                }
                Result.State.SUCCESS -> {
                    binding.loadingPb.visibility = View.GONE
                    if (result.data == null || result.data.isEmpty()) {
                        Timber.d("Result is empty.")
                        showSnackBar("No movies!")
                        movieAdapter.updateData(emptyList())
                    } else {
                        Timber.d("Movies loaded successfully: ${result.data}")
                        movieAdapter.updateData(result.data)
                    }
                }
                Result.State.ERROR -> {
                    binding.loadingPb.visibility = View.GONE
                    if (viewModel.utils.hasNetworkConnection()) {
                        Timber.e("Error while loading movies: ${result.error}")
                        showSnackBar(result.error)
                    } else {
                        Timber.w("No network connection.")
                        showSnackBar("No connection!")
                    }
                }
            }

        })

    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.rvMovies, message, Snackbar.LENGTH_LONG)
                .setAction("Retry") { viewModel.retry() }
                .show()
    }

    companion object {
        const val EXTRA_MOVIE = "movie"
        private const val MOVIE_GRID_COLUMNS = 2
    }

}
