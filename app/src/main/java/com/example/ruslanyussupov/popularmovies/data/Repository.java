package com.example.ruslanyussupov.popularmovies.data;


import android.support.annotation.Nullable;

import com.example.ruslanyussupov.popularmovies.App;
import com.example.ruslanyussupov.popularmovies.Utils;
import com.example.ruslanyussupov.popularmovies.data.local.FavouriteMoviesDb;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.data.model.MoviesResponse;
import com.example.ruslanyussupov.popularmovies.data.model.Review;
import com.example.ruslanyussupov.popularmovies.data.model.ReviewsResponse;
import com.example.ruslanyussupov.popularmovies.data.model.Video;
import com.example.ruslanyussupov.popularmovies.data.model.VideosResponse;
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbService;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

public class Repository implements DataSource {

    @Inject
    TheMovieDbService movieDbAPI;

    @Inject
    FavouriteMoviesDb favouriteMoviesDb;

    @Inject
    Utils utils;

    public Repository() {
        App.getComponent().inject(this);

    }

    @Override
    public Observable<List<Movie>> getMovies(Filter filter, int page) {

        if (filter.equals(Filter.POPULAR)) {
            return movieDbAPI.getPopularMovies(page)
                    .map(MoviesResponse::getResults);
        } else if (filter.equals(Filter.TOP_RATED)) {
            return movieDbAPI.getTopRatedMovies(page)
                    .map(MoviesResponse::getResults);
        } else {
            return favouriteMoviesDb.movieDao().getFavouriteMovies().toObservable();
        }

    }

    @Override
    public Observable<List<Video>> getMovieTrailers(int movieId) {
        return movieDbAPI.getMovieTrailers(movieId)
                .map(VideosResponse::getResults);
    }

    @Override
    public Observable<List<Review>> getMovieReviews(int movieId) {
        return movieDbAPI.getMovieReviews(movieId)
                .map(ReviewsResponse::getResults);
    }

    @Override
    public void deleteFromFavourite(Movie movie) {
        favouriteMoviesDb.movieDao().delete(movie);
        utils.deleteFile(movie.getBackdropLocalPath());
        utils.deleteFile(movie.getPosterLocalPath());
    }

    @Override
    public void addToFavourite(Movie movie) {
        String backdropLocalPath = utils.saveBitmap(movie.getFullBackdropPath(), "backdrop-" + movie.getId());
        String posterLocalPath = utils.saveBitmap(movie.getFullPosterPath(), "poster-" + movie.getId());
        movie.setBackdropLocalPath(backdropLocalPath);
        movie.setPosterLocalPath(posterLocalPath);
        favouriteMoviesDb.movieDao().insert(movie);
    }

    @Override
    @Nullable
    public Single<Movie> getFavouriteMovie(int movieId) {
        return favouriteMoviesDb.movieDao().getFavouriteMovie(movieId);
    }

}
