package com.example.ruslanyussupov.popularmovies.data

import androidx.paging.PagedList
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.data.remote.MoviesRequest
import timber.log.Timber

class MovieBoundaryCallback(private val moviesRequest: MoviesRequest)
    : PagedList.BoundaryCallback<Movie>() {

    override fun onZeroItemsLoaded() {
        Timber.d("onZeroItemsLoaded")
        moviesRequest.resetPage()
        moviesRequest.request()
    }

    override fun onItemAtEndLoaded(itemAtEnd: Movie) {
        Timber.d("onItemAtEndLoaded")
        moviesRequest.request()
    }

}