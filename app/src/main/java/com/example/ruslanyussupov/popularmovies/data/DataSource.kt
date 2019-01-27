package com.example.ruslanyussupov.popularmovies.data


import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.data.model.Video

import io.reactivex.Observable
import io.reactivex.Single

interface DataSource {

    fun getMovies(filter: Filter, page: Int): Observable<List<Movie>>

    fun getMovieTrailers(movieId: Int): Observable<List<Video>>

    fun getMovieReviews(movieId: Int): Observable<List<Review>>

    fun deleteFromFavourite(movie: Movie)

    fun addToFavourite(movie: Movie)

    fun getFavouriteMovie(movieId: Int): Single<Movie>

    enum class Filter {
        POPULAR, TOP_RATED, FAVOURITE
    }

}
