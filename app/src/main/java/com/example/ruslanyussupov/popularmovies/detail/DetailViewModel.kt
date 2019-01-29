package com.example.ruslanyussupov.popularmovies.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.Result
import com.example.ruslanyussupov.popularmovies.Utils
import com.example.ruslanyussupov.popularmovies.data.DataSource
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.data.model.Video

import javax.inject.Inject

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailViewModel(private val movie: Movie) : ViewModel() {

    @Inject
    internal lateinit var dataSource: DataSource

    @Inject
    internal lateinit var utils: Utils

    val videosResultLiveData: MutableLiveData<Result<List<Video>>> = MutableLiveData()
    val reviewsResultLiveData: MutableLiveData<Result<List<Review>>> = MutableLiveData()
    val isFavouriteLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val compositeDisposable = CompositeDisposable()

    init {
        App.component?.inject(this)
        subscribeToVideosDataSource()
        subscribeToReviewsDataSource()
        checkFavourite()
    }

    private fun subscribeToVideosDataSource() {
        compositeDisposable.add(
            dataSource
                .getMovieTrailers(movie.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { videos -> videosResultLiveData.setValue(Result.success(videos)) },
                    { error -> videosResultLiveData.setValue(Result.error(error.message ?: "")) }
                )
        )
    }

    private fun subscribeToReviewsDataSource() {
        compositeDisposable.add(
            dataSource
                .getMovieReviews(movie.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { reviews -> reviewsResultLiveData.setValue(Result.success(reviews)) },
                    { error -> reviewsResultLiveData.setValue(Result.error(error.message ?: "")) }
                )
        )
    }

    private fun checkFavourite() {
        compositeDisposable.addAll(
                dataSource.getFavouriteMovie(movie.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            isFavouriteLiveData.postValue(true)
                        }, {
                            isFavouriteLiveData.postValue(false)
                        })
        )
    }

    suspend fun addToFavourites() {
        withContext(Dispatchers.IO) { dataSource.addToFavourite(movie) }

    }

    suspend fun deleteFromFavourites() {
        withContext(Dispatchers.IO) { dataSource.deleteFromFavourite(movie) }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}
