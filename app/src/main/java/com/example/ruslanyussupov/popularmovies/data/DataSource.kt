package com.example.ruslanyussupov.popularmovies.data


import androidx.paging.PagedList
import com.example.ruslanyussupov.popularmovies.data.model.*

interface DataSource {

    suspend fun getMovies(filter: Filter, pageSize: Int = 10): Listing<PagedList<Movie>>

    suspend fun getMovieVideos(filter: Filter, movieId: Int): Listing<List<Video>>

    suspend fun getMovieReviews(filter: Filter, movieId: Int, pageSize: Int = 10): Listing<PagedList<Review>>

    suspend fun deleteFromFavourite(movie: Movie)

    suspend fun addToFavourite(movie: Movie)

    suspend fun getFavourite(movieId: Int): Movie?

    enum class Filter {
        POPULAR, TOP_RATED, FAVOURITE
    }

}
