package com.example.ruslanyussupov.popularmovies.browse


import androidx.lifecycle.*
import androidx.paging.PagedList

import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.Utils
import com.example.ruslanyussupov.popularmovies.data.DataSource
import com.example.ruslanyussupov.popularmovies.data.model.Movie

import javax.inject.Inject

import timber.log.Timber

import com.example.ruslanyussupov.popularmovies.data.DataSource.*
import com.example.ruslanyussupov.popularmovies.data.Listing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {

    @Inject
    internal lateinit var dataSource: DataSource

    @Inject
    internal lateinit var utils: Utils

    private val _filter = MutableLiveData<Filter>(Filter.POPULAR)
    val filter: LiveData<Filter> = _filter
    private val result: LiveData<Listing<PagedList<Movie>>> = Transformations.switchMap(_filter) {

        val listing = MutableLiveData<Listing<PagedList<Movie>>>()
        viewModelScope.launch(Dispatchers.IO) {
            listing.postValue(dataSource.getMovies(it))
        }
        listing
    }

    val pagedList = Transformations.switchMap(result) { it.data }

    val networkState = Transformations.switchMap(result) { it.networkState }

    val refreshState = Transformations.switchMap(result) { it.refreshState }

    init {
        App.component?.inject(this)
    }

    fun filterChanged(value: Filter) {
        if (_filter.value != value) {
            _filter.value = value
        }
    }

    fun retry() {
        result.value?.retry?.invoke()
    }

    fun refresh() {
        result.value?.refresh?.invoke()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
    }
}