package com.example.ruslanyussupov.popularmovies.data


import androidx.paging.PagedList
import com.example.ruslanyussupov.popularmovies.data.model.*

interface DataSource {

    suspend fun getMovies(filter: Filter, pageSize: Int = 10): Listing<PagedList<Movie>>

    suspend fun getMovieVideos(filter: Filter, movieId: Int): Listing<List<Video>>

    suspend fun getMovieReviews(filter: Filter, movieId: Int, pageSize: Int = 10): Listing<PagedList<Review>>

    suspend fun deleteFavourite(movie: Movie)

    suspend fun insertFavourite(movie: Movie)

    suspend fun getFavourite(movieId: Int): Favorite?

    suspend fun savePopularMovies(movies: List<Movie>)

    suspend fun saveTopRatedMovies(movies: List<Movie>)

    suspend fun saveReviews(movieId: Int, reviews: List<Review>)

    suspend fun saveVideos(movieId: Int, videos: List<Video>)

    suspend fun deletePopularMovies()

    suspend fun deleteTopRatedMovies()

    suspend fun deleteReviews(movieId: Int)

    suspend fun deleteVideos(movieId: Int)

    enum class Filter {
        POPULAR, TOP_RATED, FAVOURITE
    }

}
