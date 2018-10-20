package com.example.ruslanyussupov.popularmovies.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.ruslanyussupov.popularmovies.App;
import com.example.ruslanyussupov.popularmovies.IoScheduler;
import com.example.ruslanyussupov.popularmovies.Result;
import com.example.ruslanyussupov.popularmovies.Utils;
import com.example.ruslanyussupov.popularmovies.data.DataSource;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.data.model.Review;
import com.example.ruslanyussupov.popularmovies.data.model.Video;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DetailViewModel extends ViewModel {

    @Inject
    DataSource dataSource;

    @Inject
    IoScheduler ioScheduler;

    @Inject
    Utils utils;

    private final Movie movie;
    private final MutableLiveData<Result<List<Video>>> videosResultLiveData;
    private final MutableLiveData<Result<List<Review>>> reviewsResultLiveData;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public DetailViewModel(Movie movie) {
        this.movie = movie;
        App.getComponent().inject(this);
        videosResultLiveData = new MutableLiveData<>();
        reviewsResultLiveData = new MutableLiveData<>();
        subscribeToVideosDataSource();
        subscribeToReviewsDataSource();
    }

    public LiveData<Result<List<Video>>> getVideosResultLiveData() {
        return videosResultLiveData;
    }

    public LiveData<Result<List<Review>>> getReviewsResultLiveData() {
        return reviewsResultLiveData;
    }

    public Utils getUtils() {
        return utils;
    }

    private void subscribeToVideosDataSource() {
        compositeDisposable.add(
                dataSource.getMovieTrailers(movie.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(videos -> videosResultLiveData.setValue(Result.success(videos)),
                                error -> videosResultLiveData.setValue(Result.error(error.getMessage())))
        );
    }

    private void subscribeToReviewsDataSource() {
        compositeDisposable.add(
                dataSource.getMovieReviews(movie.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(reviews -> reviewsResultLiveData.setValue(Result.success(reviews)),
                                error -> reviewsResultLiveData.setValue(Result.error(error.getMessage())))
        );
    }

    public void addToFavourites() {
        ioScheduler.runOnThread(() -> dataSource.addToFavourite(movie));
    }

    public void deleteFromFavourites() {
        ioScheduler.runOnThread(() -> dataSource.deleteFromFavourite(movie));
    }

    public Single<Movie> getMovieFromFavourites() {
        return dataSource.getFavouriteMovie(movie.getId());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

}
