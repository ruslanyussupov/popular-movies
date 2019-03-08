package com.example.ruslanyussupov.popularmovies.data.remote

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.data.DataSource
import com.example.ruslanyussupov.popularmovies.data.NetworkState
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.data.model.ReviewsResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ReviewsRequest(private val movieId: Int) {

    @Inject
    internal lateinit var apiService: TheMovieDbService

    @Inject
    internal lateinit var requestPrefs: SharedPreferences

    @Inject
    internal lateinit var repository: DataSource

    private val pageKey = "${movieId}_movie_reviews_page"
    private var isRequestInProgress = false
    private var currentCall: Call<ReviewsResponse>? = null
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
            repository.deleteReviews(movieId)
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

        isRequestInProgress = true
        networkState.value = NetworkState.LOADING
        var page = requestPrefs.getInt(pageKey, 1)

        val call = apiService.getMovieReviews(movieId, page)
        currentCall = call
        call.enqueue(object : Callback<ReviewsResponse> {
            override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message))
                isRequestInProgress = false
            }

            override fun onResponse(call: Call<ReviewsResponse>, response: Response<ReviewsResponse>) {
                if (!response.isSuccessful) {
                    networkState.postValue(NetworkState.error("Unexpected code $response"))
                    isRequestInProgress = false
                    return
                }
                val reviewResponse = response.body()
                when {
                    reviewResponse == null -> {
                        networkState.postValue(NetworkState.error("Reviews response is null."))
                        isRequestInProgress = false
                    }
                    reviewResponse.results.isNullOrEmpty() -> {
                        networkState.postValue(NetworkState.error("Result is null."))
                        isRequestInProgress = false
                    }
                    else -> {
                        GlobalScope.launch {
                            saveReviews(reviewResponse.results)
                            requestPrefs.edit { putInt(pageKey, ++page) }
                            networkState.postValue(NetworkState.LOADED)
                            isRequestInProgress = false
                        }
                    }
                }
            }

        })
    }

    private fun saveReviews(reviews: List<Review>) {
        GlobalScope.launch {
            repository.saveReviews(movieId, reviews)
        }
    }

    private fun cancel() {
        if (currentCall == null || currentCall?.isCanceled == true) return
        currentCall?.cancel()
        isRequestInProgress = false
    }

}