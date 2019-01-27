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

    private lateinit var mMovieAdapter: MovieAdapter
    private lateinit var mMovieClickListener: OnMovieClickListener
    private lateinit var mBinding: FragmentMovieGridBinding
    private lateinit var mViewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMovieClickListener) {
            mMovieClickListener = context
        } else {
            throw ClassCastException("$context must implement OnMovieClickListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Timber.d("onCreateView")
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_grid, container, false)
        return mBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Timber.d("onActivityCreated")

        mMovieAdapter = MovieAdapter(emptyList(), mMovieClickListener::onMovieClick)
        mBinding.rvMovies.adapter = mMovieAdapter
        mBinding.rvMovies.layoutManager = GridLayoutManager(activity, MOVIE_GRID_COLUMNS)
        val offset = resources.getDimensionPixelOffset(R.dimen.movie_item_offset)
        mBinding.rvMovies.addItemDecoration(ItemDecoration(offset, offset, offset, offset))

        mViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        mViewModel.getResultLiveData().observe(this, Observer<Result<List<Movie>>> { result ->

            mBinding.rvMovies.scrollToPosition(0)

            when (result.state) {
                Result.State.LOADING -> {
                    Timber.d("Movies loading...")
                    mBinding.loadingPb.visibility = View.VISIBLE
                }
                Result.State.SUCCESS -> {
                    mBinding.loadingPb.visibility = View.GONE
                    if (result.data == null || result.data.isEmpty()) {
                        Timber.d("Result is empty.")
                        showSnackBar("No movies!")
                        mMovieAdapter.updateData(emptyList())
                    } else {
                        Timber.d("Movies loaded successfully: ${result.data}")
                        mMovieAdapter.updateData(result.data)
                    }
                }
                Result.State.ERROR -> {
                    mBinding.loadingPb.visibility = View.GONE
                    if (mViewModel.utils.hasNetworkConnection()) {
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
        Snackbar.make(mBinding.rvMovies, message, Snackbar.LENGTH_LONG)
                .setAction("Retry") { mViewModel.retry() }
                .show()
    }

    companion object {

        const val EXTRA_MOVIE = "movie"

        private const val MOVIE_GRID_COLUMNS = 2
    }

}
