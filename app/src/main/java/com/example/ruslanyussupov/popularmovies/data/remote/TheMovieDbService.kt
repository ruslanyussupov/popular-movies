package com.example.ruslanyussupov.popularmovies.data.remote

import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.data.model.MoviesResponse
import com.example.ruslanyussupov.popularmovies.data.model.ReviewsResponse
import com.example.ruslanyussupov.popularmovies.data.model.VideosResponse

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDbService {

    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Call<MoviesResponse>

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("page") page: Int): Call<MoviesResponse>

    @GET("movie/{id}")
    fun getMovie(@Path("id") id: Int): Call<Movie>

    @GET("movie/{id}/videos")
    fun getMovieVideos(@Path("id") id: Int): Call<VideosResponse>

    @GET("movie/{id}/reviews")
    fun getMovieReviews(@Path("id") id: Int,
                        @Query("page") page: Int): Call<ReviewsResponse>

    companion object {
        const val ENDPOINT = "https://api.themoviedb.org/3/"
    }

}
