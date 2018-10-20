package com.example.ruslanyussupov.popularmovies.data.remote;

import com.example.ruslanyussupov.popularmovies.data.model.MoviesResponse;
import com.example.ruslanyussupov.popularmovies.data.model.ReviewsResponse;
import com.example.ruslanyussupov.popularmovies.data.model.VideosResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDbService {

    String ENDPOINT = "https://api.themoviedb.org/3/";

    @GET("movie/popular")
    Observable<MoviesResponse> getPopularMovies(@Query("page") int page);

    @GET("movie/top_rated")
    Observable<MoviesResponse> getTopRatedMovies(@Query("page") int page);

    @GET("movie/{id}/videos")
    Observable<VideosResponse> getMovieTrailers(@Path("id") int id);

    @GET("movie/{id}/reviews")
    Observable<ReviewsResponse> getMovieReviews(@Path("id") int id);

}
