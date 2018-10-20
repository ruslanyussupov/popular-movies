package com.example.ruslanyussupov.popularmovies.data;



import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.data.model.Review;
import com.example.ruslanyussupov.popularmovies.data.model.Video;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface DataSource {

    Observable<List<Movie>> getMovies(Filter filter, int page);

    Observable<List<Video>> getMovieTrailers(int movieId);

    Observable<List<Review>> getMovieReviews(int movieId);

    void deleteFromFavourite(Movie movie);

    void addToFavourite(Movie movie);

    Single<Movie> getFavouriteMovie(int movieId);

    enum Filter {
        POPULAR, TOP_RATED, FAVOURITE
    }

}
