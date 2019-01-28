package com.example.ruslanyussupov.popularmovies.data


import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.Utils
import com.example.ruslanyussupov.popularmovies.data.local.FavouriteMoviesDb
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.data.model.Video
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbService

import javax.inject.Inject

import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber

class Repository : DataSource {

    @Inject
    internal lateinit var movieDbAPI: TheMovieDbService

    @Inject
    internal lateinit var favouriteMoviesDb: FavouriteMoviesDb

    @Inject
    internal lateinit var utils: Utils

    init {
        App.component?.inject(this)
    }

    override fun getMovies(filter: DataSource.Filter, page: Int): Observable<List<Movie>> {

        return when (filter) {
            DataSource.Filter.POPULAR -> movieDbAPI.getPopularMovies(page)
                    .map { response -> response.results }
            DataSource.Filter.TOP_RATED -> movieDbAPI.getTopRatedMovies(page)
                    .map { response -> response.results }
            else -> favouriteMoviesDb.movieDao().getFavouriteMovies().toObservable()
        }

    }

    override fun getMovieTrailers(movieId: Int): Observable<List<Video>> {
        return movieDbAPI.getMovieTrailers(movieId)
                .map { it.results }
    }

    override fun getMovieReviews(movieId: Int): Observable<List<Review>> {
        return movieDbAPI.getMovieReviews(movieId)
                .map { it.results }
    }

    override fun deleteFromFavourite(movie: Movie) {
        favouriteMoviesDb.movieDao().delete(movie)
        utils.deleteFile(movie.backdropLocalPath)
        utils.deleteFile(movie.posterLocalPath)
    }

    override fun addToFavourite(movie: Movie) {
        val backdrop = utils.loadBitmap(movie.fullBackdropPath)
        val poster = utils.loadBitmap(movie.fullPosterPath)

        Timber.d("$backdrop")
        Timber.d("$poster")

        if (backdrop != null) {
            val backdropLocalPath = utils.saveBitmap(backdrop, "backdrop-${movie.id}")
            movie.backdropLocalPath = backdropLocalPath
        }

        if (poster != null) {
            val posterLocalPath = utils.saveBitmap(poster,
                    "poster-${movie.id}")
            movie.posterLocalPath = posterLocalPath
        }

        favouriteMoviesDb.movieDao().insert(movie)
    }

    override fun getFavouriteMovie(movieId: Int): Single<Movie> {
        return favouriteMoviesDb.movieDao().getFavouriteMovie(movieId)
    }

}
