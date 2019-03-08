package com.example.ruslanyussupov.popularmovies.detail

import androidx.lifecycle.*
import androidx.paging.PagedList

import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.data.DataSource
import com.example.ruslanyussupov.popularmovies.data.DataSource.Filter
import com.example.ruslanyussupov.popularmovies.data.Listing
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.data.model.Video

import javax.inject.Inject

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailViewModel : ViewModel() {

    @Inject
    internal lateinit var dataSource: DataSource

    lateinit var movie: Movie
    lateinit var filter: Filter

    private var markDelete = false

    private val _isFavorite by lazy {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val state = dataSource.getFavourite(movie.id) != null
            result.postValue(state)
        }
        result
    }

    private val videosListing: LiveData<Listing<List<Video>>> by lazy {
        val result = MutableLiveData<Listing<List<Video>>>()
        viewModelScope.launch(Dispatchers.IO) {
            val listing = dataSource.getMovieVideos(filter, movie.id)
            result.postValue(listing)
        }
        result
    }

    private val reviewsListing: LiveData<Listing<PagedList<Review>>> by lazy {
        val result = MutableLiveData<Listing<PagedList<Review>>>()
        viewModelScope.launch(Dispatchers.IO) {
            val listing = dataSource.getMovieReviews(filter, movie.id)
            result.postValue(listing)
        }
        result
    }

    init {
        App.component?.inject(this)
    }

    fun setIsFavourite(state: Boolean) {
        if (state == _isFavorite.value) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (state) {
                addToFavourites()
            } else {
                deleteFromFavourites()
            }
            _isFavorite.postValue(state)
        }
    }

    fun videos() = Transformations.switchMap(videosListing) { it.data }

    fun videosNetworkState() = Transformations.switchMap(videosListing) { it.data }

    fun videosRefreshState() = Transformations.switchMap(videosListing) { it.refreshState }

    fun reviews() = Transformations.switchMap(reviewsListing) { it.data }

    fun reviewsNetworkState() = Transformations.switchMap(reviewsListing) { it.networkState }

    fun reviewsRefreshState() = Transformations.switchMap(reviewsListing) { it.refreshState }

    fun isFavorite() = _isFavorite

    fun retryVideos() {
        Timber.d("retryVideos")
        videosListing.value?.retry?.invoke()
    }

    fun retryReviews() {
        Timber.d("retryReviews")
        videosListing.value?.retry?.invoke()
    }

    private suspend fun addToFavourites() {
        markDelete = false
        GlobalScope.launch(Dispatchers.IO) { dataSource.insertFavourite(movie) }

    }

    private fun deleteFromFavourites() {
        markDelete = true
    }

    override fun onCleared() {
        super.onCleared()
        if (markDelete) {
            GlobalScope.launch(Dispatchers.IO) { dataSource.deleteFavourite(movie) }
        }
    }

}
