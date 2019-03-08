package com.example.ruslanyussupov.popularmovies.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.data.DataSource
import com.example.ruslanyussupov.popularmovies.data.NetworkState
import com.example.ruslanyussupov.popularmovies.data.model.Video
import com.example.ruslanyussupov.popularmovies.data.model.VideosResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class VideosRequest(private val movieId: Int) {

    @Inject
    internal lateinit var apiService: TheMovieDbService

    @Inject
    internal lateinit var repository: DataSource

    private var isRequestInProgress = false
    private var currentCall: Call<VideosResponse>? = null
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

    fun retry() {
        cancel()
        request()
    }

    fun refresh() = runBlocking {
        cancel()
        GlobalScope.launch {
            repository.deleteVideos(movieId)
        }.join()
        request(_refreshState)
    }

    private fun request(networkState: MutableLiveData<NetworkState>) {

        if (isRequestInProgress) return

        networkState.value = NetworkState.LOADING
        isRequestInProgress = true

        val call = apiService.getMovieVideos(movieId)
        currentCall = call
        call.enqueue(object : Callback<VideosResponse> {
            override fun onFailure(call: Call<VideosResponse>, t: Throwable) {
                networkState.postValue(NetworkState.error(t.message))
                isRequestInProgress = false
            }

            override fun onResponse(call: Call<VideosResponse>, response: Response<VideosResponse>) {
                val videoResponse = response.body()
                when {
                    videoResponse == null -> {
                        networkState.postValue(NetworkState.error("Video response is null."))
                        isRequestInProgress = false
                    }
                    videoResponse.results.isNullOrEmpty() -> {
                        networkState.postValue(NetworkState.error("Result is null."))
                        isRequestInProgress = false
                    }
                    else -> {
                        saveVideos(videoResponse.results)
                        networkState.postValue(NetworkState.LOADED)
                        isRequestInProgress = false
                    }
                }
            }

        })
    }

    private fun cancel() {
        if (currentCall == null || currentCall?.isCanceled == true) return
        currentCall?.cancel()
        isRequestInProgress = false
    }

    private fun saveVideos(videos: List<Video>) {
        GlobalScope.launch {
            repository.saveVideos(movieId, videos)
        }
    }

}