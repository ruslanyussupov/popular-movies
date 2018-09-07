package com.example.ruslanyussupov.popularmovies.data.remote;

import com.example.ruslanyussupov.popularmovies.data.model.MoviesResponse;
import com.example.ruslanyussupov.popularmovies.data.model.ReviewsResponse;
import com.example.ruslanyussupov.popularmovies.data.model.VideosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TheMovieDbAPI {

    String ENDPOINT = "https://api.themoviedb.org/3/";

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies();

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies();

    @GET("movie/{id}/videos")
    Call<VideosResponse> getMovieTrailers(@Path("id") int id);

    @GET("movie/{id}/reviews")
    Call<ReviewsResponse> getMovieReviews(@Path("id") int id);

}
