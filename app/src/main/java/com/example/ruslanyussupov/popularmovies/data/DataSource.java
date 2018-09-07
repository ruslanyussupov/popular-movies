package com.example.ruslanyussupov.popularmovies.data;

import android.arch.lifecycle.LiveData;

import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.data.model.Review;
import com.example.ruslanyussupov.popularmovies.data.model.Video;

import java.util.List;

public interface DataSource {

    LiveData<List<Movie>> getMovies(Filter filter);

    LiveData<List<Movie>> getFavouriteMovies();

    LiveData<List<Video>> getMovieTrailers(int movieId);

    LiveData<List<Review>> getMovieReviews(int movieId);

    enum Filter {
        POPULAR, TOP_RATED
    }

}
