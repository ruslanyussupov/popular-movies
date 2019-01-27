package com.example.ruslanyussupov.popularmovies.data.remote

import com.example.ruslanyussupov.popularmovies.data.model.MoviesResponse
import com.example.ruslanyussupov.popularmovies.data.model.ReviewsResponse
import com.example.ruslanyussupov.popularmovies.data.model.VideosResponse

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDbService {

    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Observable<MoviesResponse>

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("page") page: Int): Observable<MoviesResponse>

    @GET("movie/{id}/videos")
    fun getMovieTrailers(@Path("id") id: Int): Observable<VideosResponse>

    @GET("movie/{id}/reviews")
    fun getMovieReviews(@Path("id") id: Int): Observable<ReviewsResponse>

    companion object {
        const val ENDPOINT = "https://api.themoviedb.org/3/"
    }

}
