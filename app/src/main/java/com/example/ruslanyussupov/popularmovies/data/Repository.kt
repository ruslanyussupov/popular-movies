package com.example.ruslanyussupov.popularmovies.data


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.Utils
import com.example.ruslanyussupov.popularmovies.data.DataSource.*
import com.example.ruslanyussupov.popularmovies.data.local.MovieDb
import com.example.ruslanyussupov.popularmovies.data.model.*
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import javax.inject.Inject

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

import javax.inject.Named

class Repository : DataSource {

    @Inject
    internal lateinit var movieDbAPI: TheMovieDbService

    @Inject
    @field:Named("FavouriteDb")
    internal lateinit var favouriteDb: MovieDb

    @Inject
    @field:Named("PopularDb")
    internal lateinit var popularDb: MovieDb

    @Inject
    @field:Named("TopRatedDb")
    internal lateinit var topRatedDb: MovieDb

    @Inject
    internal lateinit var utils: Utils

    init {
        App.component?.inject(this)
    }

    override suspend fun getMovies(filter: Filter, pageSize: Int): Listing<PagedList<Movie>> {

        return when (filter) {
            Filter.POPULAR -> {
                createMoviesListing(popularDb, filter, pageSize)
            }
            Filter.TOP_RATED -> {
                createMoviesListing(topRatedDb, filter, pageSize)
            }
            Filter.FAVOURITE -> {
                val pagedList = favouriteDb.movieDao().getMovies().toLiveData(pageSize)
                Listing(pagedList,
                        MutableLiveData(NetworkState.LOADED),
                        null,
                        MutableLiveData(NetworkState.LOADED),
                        null)
            }

        }

    }

    override suspend fun getMovieVideos(filter: Filter, movieId: Int): Listing<List<Video>> {

        return when (filter) {
            Filter.POPULAR -> { createVideosListing(popularDb, movieId) }
            Filter.TOP_RATED -> { createVideosListing(topRatedDb, movieId) }
            Filter.FAVOURITE -> { createVideosListing(favouriteDb, movieId) }
        }

    }

    override suspend fun getMovieReviews(filter: Filter, movieId: Int, pageSize: Int)
            : Listing<PagedList<Review>> {

        return when (filter) {
            Filter.POPULAR -> {
                createReviewsListing(popularDb, filter, movieId, pageSize)
            }
            Filter.TOP_RATED -> {
                createReviewsListing(topRatedDb, filter, movieId, pageSize)
            }
            Filter.FAVOURITE -> {
                createReviewsListing(favouriteDb, filter, movieId, pageSize)
            }
        }

    }

    override suspend fun deleteFromFavourite(movie: Movie) {
        favouriteDb.movieDao().deleteMovie(movie)
        utils.deleteFile(movie.backdropLocalPath)
        utils.deleteFile(movie.posterLocalPath)
    }

    override suspend fun addToFavourite(movie: Movie) {
        movie.apply {
            posterLocalPath = utils.saveBitmap(movie.fullPosterPath, "favorite", "poster-${movie.id}")
            backdropLocalPath = utils.saveBitmap(movie.fullBackdropPath, "favorite", "backdrop-${movie.id}")
        }
        favouriteDb.movieDao().insertMovie(movie)

    }

    override suspend fun getFavourite(movieId: Int): Movie? {
        return favouriteDb.movieDao().getMovie(movieId)
    }

    private fun createReviewsListing(db: MovieDb, filter: Filter, movieId: Int, pageSize: Int)
            : Listing<PagedList<Review>> {

        val boundaryCallback = ReviewBoundaryCallback(movieId, filter, db, movieDbAPI)
        val pagedList = db.reviewDao().getReviews(movieId).toLiveData(
                pageSize = pageSize,
                boundaryCallback = boundaryCallback)

        return Listing(pagedList,
                boundaryCallback.networkState,
                boundaryCallback::refresh,
                boundaryCallback.refreshState,
                boundaryCallback.retry)
    }

    private fun createVideosListing(db: MovieDb, movieId: Int)
            : Listing<List<Video>> {

        val videosRequest = VideosRequest(movieId, movieDbAPI, db)

        return Listing(db.videoDao().getVideos(movieId),
                videosRequest.networkState,
                videosRequest::refresh,
                videosRequest.refreshState,
                videosRequest.retry)
    }

    private fun createMoviesListing(db: MovieDb, filter: Filter, pageSize: Int)
            : Listing<PagedList<Movie>> {

        val boundaryCallback = MovieBoundaryCallback(filter, db, movieDbAPI, utils)
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(pageSize)
                .build()
        val pagedList = db.movieDao().getMovies().toLiveData(
                config = config,
                boundaryCallback = boundaryCallback)

        return Listing(pagedList,
                boundaryCallback.networkState,
                boundaryCallback::refresh,
                boundaryCallback.refreshState,
                boundaryCallback.retry)
    }

    private class VideosRequest(private val movieId: Int,
                                private val movieDbAPI: TheMovieDbService,
                                private val db: MovieDb) {

        private var isRequestInProgress = false
        private val _networkState = MutableLiveData<NetworkState>()
        val networkState: LiveData<NetworkState> = _networkState
        private val _refreshState = MutableLiveData<NetworkState>()
        val refreshState: LiveData<NetworkState> = _refreshState
        val retry: () -> Unit = { request(_networkState) }

        private fun request(networkState: MutableLiveData<NetworkState>) {

            if (isRequestInProgress) return
            networkState.postValue(NetworkState.LOADING)

            movieDbAPI.getMovieVideos(movieId).enqueue(object : Callback<VideosResponse> {
                override fun onFailure(call: Call<VideosResponse>, t: Throwable) {
                    Timber.e(t)
                    networkState.postValue(NetworkState.error(t.message))
                    isRequestInProgress = false
                }

                override fun onResponse(call: Call<VideosResponse>, response: Response<VideosResponse>) {
                    val videoResponse = response.body()
                    if (videoResponse == null) {
                        Timber.e("Video response is null.")
                        networkState.postValue(NetworkState.error("Video response is null."))
                        isRequestInProgress = false
                    } else {
                        Timber.d("Videos loaded: ${videoResponse.results.size}")
                        GlobalScope.launch {
                            saveVideos(videoResponse.results)
                            networkState.postValue(NetworkState.LOADED)
                            isRequestInProgress = false
                        }
                    }
                }

            })
        }

        private suspend fun saveVideos(videos: List<Video>) {
            videos.onEach { it.movieId = movieId }
            db.videoDao().insertVideos(videos)
        }

        fun refresh() {
            runBlocking {
                GlobalScope.launch {
                    db.videoDao().deleteVideos(movieId)
                }.join()
                request(_refreshState)
            }
        }

    }

}
