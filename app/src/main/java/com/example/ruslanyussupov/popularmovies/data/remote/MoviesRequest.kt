package com.example.ruslanyussupov.popularmovies.data.remote

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.data.DataSource
import com.example.ruslanyussupov.popularmovies.data.DataSource.Filter
import com.example.ruslanyussupov.popularmovies.data.NetworkState
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.data.model.MoviesResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MoviesRequest(private val filter: Filter) {

    @Inject
    internal lateinit var apiService: TheMovieDbService

    @Inject
    internal lateinit var requestPrefs: SharedPreferences

    @Inject
    internal lateinit var repository: DataSource

    private val pageKey = "${filter}_movies_page"
    private var isRequestInProgress = false
    private var currentCall: Call<MoviesResponse>? = null
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState
    private val _refreshState = MutableLiveData<NetworkState>()
    val refreshState: LiveData<NetworkState> = _refreshState

    init {
        App.component?.inject(this)
    }

    fun request() {
        request(_networkState)
    }

    fun refresh() = runBlocking {
        cancel()
        requestPrefs.edit { putInt(pageKey, 1) }
        GlobalScope.launch {
            when (filter) {
                Filter.POPULAR -> { repository.deletePopularMovies() }
                Filter.TOP_RATED -> { repository.deleteTopRatedMovies() }
                else -> { throw IllegalStateException("Invalid filter $filter") }
            }
        }.join()

        request(_refreshState)
    }

    fun retry() {
        cancel()
        request()
    }

    fun resetPage() {
        requestPrefs.edit { putInt(pageKey, 1) }
    }

    private fun request(networkState: MutableLiveData<NetworkState>) {

        if (isRequestInProgress) return

        val page = requestPrefs.getInt(pageKey, 1)

        when (filter) {
            Filter.POPULAR -> {
                val call = apiService.getPopularMovies(page)
                currentCall = call
                enqueue(call, networkState)
            }
            Filter.TOP_RATED -> {
                val call = apiService.getTopRatedMovies(page)
                currentCall = call
                enqueue(call, networkState)
            }
            else -> { throw IllegalStateException("Invalid filter $filter") }
        }

    }

    private fun enqueue(call: Call<MoviesResponse>, networkState: MutableLiveData<NetworkState>) {

        isRequestInProgress = true
        networkState.value = NetworkState.LOADING
        var page = requestPrefs.getInt(pageKey, 1)
        call.enqueue(object : Callback<MoviesResponse> {
            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message))
                isRequestInProgress = false
            }

            override fun onResponse(call: Call<MoviesResponse>, response: Response<MoviesResponse>) {
                if (!response.isSuccessful) {
                    networkState.postValue(NetworkState.error("Unexpected code $response"))
                    isRequestInProgress = false
                    return
                }
                val moviesResponse = response.body()
                when {
                    moviesResponse == null -> {
                        networkState.postValue(NetworkState.error("$filter movies response is null."))
                        isRequestInProgress = false
                    }
                    moviesResponse.movies.isNullOrEmpty() -> {
                        networkState.postValue(NetworkState.error("$filter result is null."))
                        isRequestInProgress = false
                    }
                    else -> GlobalScope.launch {
                        saveMovies(moviesResponse.movies)
                        requestPrefs.edit { putInt(pageKey, ++page) }
                        networkState.postValue(NetworkState.LOADED)
                        isRequestInProgress = false
                    }
                }
            }

        })
    }

    private suspend fun saveMovies(movies: List<Movie>) {
        when (filter) {
            Filter.POPULAR -> { repository.savePopularMovies(movies) }
            Filter.TOP_RATED -> { repository.saveTopRatedMovies(movies) }
            else -> { throw IllegalStateException("Invalid filter $filter") }
        }
    }

    private fun cancel() {
        if (currentCall == null || currentCall?.isCanceled == true) return
        currentCall?.cancel()
        isRequestInProgress = false
    }

}