package com.example.ruslanyussupov.popularmovies.data.remote;

import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.data.model.MoviesResponse;
import com.example.ruslanyussupov.popularmovies.data.model.Review;
import com.example.ruslanyussupov.popularmovies.data.model.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TheMovieDbAPI {

    static final String ENDPOINT = "https://api.themoviedb.org/3/";

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies();

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies();

    @GET("movie/{id}")
    Call<Movie> getMovieDetails(@Path("id") int id);

    @GET("movie/{id}/videos")
    Call<List<Video>> getMovieTrailers(@Path("id") int id);

    @GET("movie/{id}/reviews")
    Call<List<Review>> getMovieReviews(@Path("id") int id);

}
