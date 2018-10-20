package com.example.ruslanyussupov.popularmovies.list;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.ruslanyussupov.popularmovies.App;
import com.example.ruslanyussupov.popularmovies.Result;
import com.example.ruslanyussupov.popularmovies.Utils;
import com.example.ruslanyussupov.popularmovies.data.DataSource;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

import static com.example.ruslanyussupov.popularmovies.data.DataSource.*;


public class MainViewModel extends ViewModel {

    @Inject
    DataSource dataSource;

    @Inject
    Utils utils;

    private final MutableLiveData<Result<List<Movie>>> resultLiveData;
    private BehaviorSubject<Filter> filterSubject = BehaviorSubject.create();
    private BehaviorSubject<Object> retrySubject = BehaviorSubject.createDefault(new Object());
    private Disposable disposable;
    private Disposable favouritesDisposable;

    public MainViewModel() {
        App.getComponent().inject(this);
        resultLiveData = new MutableLiveData<>();
        subscribe();
    }

    public LiveData<Result<List<Movie>>> getResultLiveData() {
        return resultLiveData;
    }

    public Utils getUtils() {
        return utils;
    }

    public void onFilterChanged(Filter filter) {
        filterSubject.onNext(filter);
/*        if (disposable.isDisposed()) {
            subscribe();
        }*/
    }

    public void retry() {
        Timber.d("Retry.");
/*        if (!disposable.isDisposed()) {
            disposable.dispose();
        }*/
        retrySubject.onNext(new Object());
    }

    private void subscribe() {
        disposable = Observable.combineLatest(filterSubject.distinctUntilChanged(), retrySubject, (filter, retry) -> filter)
                .subscribe(filter -> {
                    if (filter == Filter.FAVOURITE) {
                        resultLiveData.postValue(Result.loading());
                        favouritesDisposable = dataSource.getMovies(filter, 1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(movies -> resultLiveData.setValue(Result.success(movies)),
                                        error -> resultLiveData.setValue(Result.error(error.getMessage())));
                    } else {
                        if (favouritesDisposable != null && !favouritesDisposable.isDisposed()) {
                            favouritesDisposable.dispose();
                        }
                        resultLiveData.postValue(Result.loading());
                        dataSource.getMovies(filter, 1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(movies -> resultLiveData.setValue(Result.success(movies)),
                                        error -> resultLiveData.setValue(Result.error(error.getMessage())));
                    }
                }, Timber::e);

   /*     disposable = filterSubject.distinctUntilChanged()
                .observeOn(Schedulers.io())
                .flatMap(filter -> {
                    resultLiveData.postValue(Result.loading());
                    return dataSource.getMovies(filter, 1);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> resultLiveData.setValue(Result.success(movies)),
                        error -> resultLiveData.setValue(Result.error(error.getMessage())));*/
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Timber.d("onCleared");
        disposable.dispose();
        if (favouritesDisposable != null && !favouritesDisposable.isDisposed()) {
            favouritesDisposable.dispose();
        }
    }
}
