package com.example.ruslanyussupov.popularmovies.list


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.Result
import com.example.ruslanyussupov.popularmovies.Utils
import com.example.ruslanyussupov.popularmovies.data.DataSource
import com.example.ruslanyussupov.popularmovies.data.model.Movie

import javax.inject.Inject

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

import com.example.ruslanyussupov.popularmovies.data.DataSource.*
import io.reactivex.functions.BiFunction


class MainViewModel : ViewModel() {

    @Inject
    internal lateinit var dataSource: DataSource

    @Inject
    internal lateinit var utils: Utils

    private val resultLiveData: MutableLiveData<Result<List<Movie>>>
    private val filterSubject: BehaviorSubject<Filter> = BehaviorSubject.create()
    private val retrySubject: BehaviorSubject<Any> = BehaviorSubject.createDefault(Any())
    private var disposable: Disposable? = null
    private var favouritesDisposable: Disposable? = null

    init {
        App.component?.inject(this)
        resultLiveData = MutableLiveData()
        subscribe()
    }

    fun getResultLiveData(): LiveData<Result<List<Movie>>> {
        return resultLiveData
    }

    fun onFilterChanged(filter: Filter) {
        filterSubject.onNext(filter)
    }

    fun retry() {
        Timber.d("Retry.")
        retrySubject.onNext(Any())
    }

    private fun subscribe() {
        disposable = Observable.combineLatest(filterSubject.distinctUntilChanged(),
                retrySubject, BiFunction<Filter, Any, Filter> { filter, _ -> filter })
                .subscribe({ filter ->
                    if (filter == Filter.FAVOURITE) {
                        resultLiveData.postValue(Result.loading())
                        favouritesDisposable = dataSource.getMovies(filter, 1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { movies -> resultLiveData.setValue(Result.success(movies)) },
                                        { error -> resultLiveData.setValue(Result.error(error.message ?: "")) }
                                )
                    } else {
                        if (favouritesDisposable?.isDisposed == false) {
                            favouritesDisposable?.dispose()
                        }
                        resultLiveData.postValue(Result.loading())
                        dataSource.getMovies(filter, 1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { movies -> resultLiveData.setValue(Result.success(movies)) },
                                        { error -> resultLiveData.setValue(Result.error(error.message ?: "")) }
                                )
                    }
                }, { Timber.e(it) })

    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
        disposable?.dispose()
        if (favouritesDisposable?.isDisposed == false) favouritesDisposable?.dispose()
    }
}