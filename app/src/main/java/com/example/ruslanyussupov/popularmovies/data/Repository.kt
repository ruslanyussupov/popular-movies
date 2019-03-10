package com.example.ruslanyussupov.popularmovies.data


import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.PicturesManager
import com.example.ruslanyussupov.popularmovies.data.DataSource.*
import com.example.ruslanyussupov.popularmovies.data.local.MovieDb
import com.example.ruslanyussupov.popularmovies.data.model.*
import com.example.ruslanyussupov.popularmovies.data.remote.MoviesRequest
import com.example.ruslanyussupov.popularmovies.data.remote.ReviewsRequest
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbService
import com.example.ruslanyussupov.popularmovies.data.remote.VideosRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import javax.inject.Inject

class Repository : DataSource {

    @Inject
    internal lateinit var movieDbAPI: TheMovieDbService

    @Inject
    internal lateinit var movieDb: MovieDb

    @Inject
    internal lateinit var picturesManager: PicturesManager

    init {
        App.component?.inject(this)
    }

    override suspend fun getMovies(filter: Filter, pageSize: Int): Listing<PagedList<Movie>> {

        return when (filter) {
            Filter.POPULAR -> {
                createPopularMoviesListing(filter, pageSize)
            }
            Filter.TOP_RATED -> {
                createTopRatedMoviesListing(filter, pageSize)
            }
            Filter.FAVOURITE -> {
                val pagedList = movieDb.movieDao().getFavoriteMovies().toLiveData(pageSize)
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
            Filter.POPULAR -> { createVideosListing(movieId) }
            Filter.TOP_RATED -> { createVideosListing(movieId) }
            Filter.FAVOURITE -> { createVideosListing(movieId) }
        }

    }

    override suspend fun getMovieReviews(filter: Filter, movieId: Int, pageSize: Int)
            : Listing<PagedList<Review>> {

        return when (filter) {
            Filter.POPULAR -> {
                createReviewsListing(movieId, pageSize)
            }
            Filter.TOP_RATED -> {
                createReviewsListing(movieId, pageSize)
            }
            Filter.FAVOURITE -> {
                createReviewsListing(movieId, pageSize)
            }
        }

    }

    override suspend fun insertFavourite(movie: Movie) {
        movie.apply {
            posterLocalPath = savePoster(movie.fullPosterPath, movie.id)
            backdropLocalPath = saveBackdrop(movie.fullBackdropPath, movie.id)
        }
        movieDb.movieDao().insertFavoriteMovie(movie)
    }

    override suspend fun deleteFavourite(movie: Movie) {
        val id = movieDb.movieDao().deleteFavoriteMovie(movie)
        if (id > -1) {
            deletePoster(id)
            deleteBackdrop(id)
        }
    }

    override suspend fun savePopularMovies(movies: List<Movie>) {
        val jobs = mutableListOf<Job>()
        movies.forEach {
            jobs += GlobalScope.launch {
                it.posterLocalPath = savePoster(it.fullPosterPath, it.id)
                it.backdropLocalPath = saveBackdrop(it.fullBackdropPath, it.id)
            }
        }
        jobs.forEach { it.join() }
        movieDb.movieDao().insertPopularMovies(movies)
    }

    override suspend fun saveTopRatedMovies(movies: List<Movie>) {
        val jobs = mutableListOf<Job>()
        movies.forEach {
            jobs += GlobalScope.launch {
                it.posterLocalPath = savePoster(it.fullPosterPath, it.id)
                it.backdropLocalPath = saveBackdrop(it.fullBackdropPath, it.id)
            }
        }
        jobs.forEach { it.join() }
        movieDb.movieDao().insertTopRatedMovies(movies)
    }

    override suspend fun saveReviews(movieId: Int, reviews: List<Review>) {
        var index = movieDb.reviewDao().getMaxIndex(movieId) ?: -1
        reviews.forEach {
            it.movieId = movieId
            it.indexInResponse = ++index
        }
        movieDb.reviewDao().insertReviews(reviews)
    }

    override suspend fun saveVideos(movieId: Int, videos: List<Video>) {
        videos.forEach { it.movieId = movieId }
        movieDb.videoDao().insertVideos(videos)
    }

    override suspend fun getFavourite(movieId: Int): Favorite? {
        return movieDb.movieDao().getFavorite(movieId)
    }

    override suspend fun deletePopularMovies() {
        val ids = movieDb.movieDao().deletePopularMovies()
        deletePosters(ids)
        deleteBackdrops(ids)
    }

    override suspend fun deleteTopRatedMovies() {
        val ids = movieDb.movieDao().deleteTopRatedMovies()
        deletePosters(ids)
        deleteBackdrops(ids)
    }

    override suspend fun deleteReviews(movieId: Int) {
        movieDb.reviewDao().deleteReviews(movieId)
    }

    override suspend fun deleteVideos(movieId: Int) {
        movieDb.videoDao().deleteVideos(movieId)
    }

    private fun createReviewsListing(movieId: Int, pageSize: Int)
            : Listing<PagedList<Review>> {

        val reviewsRequest = ReviewsRequest(movieId)
        val boundaryCallback = ReviewBoundaryCallback(reviewsRequest)
        val pagedList = movieDb.reviewDao().getReviews(movieId).toLiveData(
                pageSize = pageSize,
                boundaryCallback = boundaryCallback)

        return Listing(pagedList,
                reviewsRequest.networkState,
                reviewsRequest::refresh,
                reviewsRequest.refreshState,
                reviewsRequest::retry)
    }

    private fun createVideosListing(movieId: Int)
            : Listing<List<Video>> {

        val videosRequest = VideosRequest(movieId)

        return Listing(movieDb.videoDao().getVideos(movieId),
                videosRequest.networkState,
                videosRequest::refresh,
                videosRequest.refreshState,
                videosRequest::retry)
    }

    private fun createPopularMoviesListing(filter: Filter, pageSize: Int)
            : Listing<PagedList<Movie>> {

        val moviesRequest = MoviesRequest(filter)
        val boundaryCallback = MovieBoundaryCallback(moviesRequest)
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(pageSize)
                .build()
        val pagedList = movieDb.movieDao().getPopularMovies().toLiveData(
                config = config,
                boundaryCallback = boundaryCallback)

        return Listing(pagedList,
                moviesRequest.networkState,
                moviesRequest::refresh,
                moviesRequest.refreshState,
                moviesRequest::retry)
    }

    private fun createTopRatedMoviesListing(filter: Filter, pageSize: Int)
            : Listing<PagedList<Movie>> {

        val moviesRequest = MoviesRequest(filter)
        val boundaryCallback = MovieBoundaryCallback(moviesRequest)
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(pageSize)
                .build()
        val pagedList = movieDb.movieDao().getTopRatedMovies().toLiveData(
                config = config,
                boundaryCallback = boundaryCallback)

        return Listing(pagedList,
                moviesRequest.networkState,
                moviesRequest::refresh,
                moviesRequest.refreshState,
                moviesRequest::retry)
    }

    private fun savePoster(url: String, id: Int): String? {
        return picturesManager.saveBitmap(url, POSTERS_DIR, "poster-$id")
    }

    private fun saveBackdrop(url: String, id: Int): String? {
        return picturesManager.saveBitmap(url, BACKDROPS_DIR, "backdrop-$id")
    }

    private fun deletePosters(ids: List<Int>) {
        ids.forEach {
            picturesManager.delete(POSTERS_DIR, "poster-$it")
        }
    }

    private fun deleteBackdrops(ids: List<Int>) {
        ids.forEach {
            picturesManager.delete(BACKDROPS_DIR, "backdrop-$it")
        }
    }

    private fun deletePoster(id: Int) {
        picturesManager.delete(POSTERS_DIR, "poster-$id")
    }

    private fun deleteBackdrop(id: Int) {
        picturesManager.delete(BACKDROPS_DIR, "backdrop-$id")
    }

    companion object {
        private const val POSTERS_DIR = "posters"
        private const val BACKDROPS_DIR = "backdrops"
    }

}
