package com.example.ruslanyussupov.popularmovies.data

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.data.DataSource.Filter
import com.example.ruslanyussupov.popularmovies.data.local.MovieDb
import com.example.ruslanyussupov.popularmovies.data.model.ReviewsResponse
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject


class ReviewBoundaryCallback(private val movieId: Int,
                             private val filter: Filter,
                             private val db: MovieDb,
                             private val movieDbAPI: TheMovieDbService) : PagedList.BoundaryCallback<Review>() {

    private var isRequestInProgress = false
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState
    private val _refreshState = MutableLiveData<NetworkState>()
    val refreshState: LiveData<NetworkState> = _refreshState
    val retry = { requestAndSaveReviews(_networkState) }

    @Inject
    internal lateinit var requestPrefs: SharedPreferences

    init {
        App.component?.inject(this)
    }

    override fun onZeroItemsLoaded() {
        Timber.d("onZeroItemsLoaded")
        requestAndSaveReviews(_networkState)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Review) {
        Timber.d("onItemAtEndLoaded")
        requestAndSaveReviews(_networkState)
    }

    private fun requestAndSaveReviews(networkState: MutableLiveData<NetworkState>) {

        if (isRequestInProgress) return

        isRequestInProgress = true

        val page = requestPrefs.getInt("${filter}_${movieId}_movie_reviews_page", 1)
        networkState.postValue(NetworkState.LOADING)

        movieDbAPI.getMovieReviews(movieId, page).enqueue(object : Callback<ReviewsResponse> {
            override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                Timber.e(t)
                networkState.postValue(NetworkState.error(t.message))
                isRequestInProgress = false
            }

            override fun onResponse(call: Call<ReviewsResponse>, response: Response<ReviewsResponse>) {
                val reviewsResponse = response.body()
                if (reviewsResponse == null) {
                    Timber.e("Reviews response is null.")
                    networkState.postValue(NetworkState.error("Reviews response is null."))
                    isRequestInProgress = false
                } else {
                    Timber.d("Reviews loaded: ${reviewsResponse.results.size}")
                    GlobalScope.launch {
                        saveReviews(reviewsResponse.results)
                        if (!reviewsResponse.results.isNullOrEmpty()) {
                            requestPrefs.edit { putInt("${filter}_${movieId}_movie_reviews_page", page + 1) }
                        }
                        networkState.postValue(NetworkState.LOADED)
                        isRequestInProgress = false
                    }
                }
            }

        })

    }

    private suspend fun saveReviews(reviews: List<Review>) {
        var index = db.reviewDao().getMaxIndex() ?: -1
        reviews.onEach {
            it.movieId = movieId
            it.indexInResponse = ++index
        }
        db.reviewDao().insertReviews(reviews)
    }

    fun refresh()  = runBlocking {
        GlobalScope.launch {
            db.reviewDao().deleteReviews()
        }.join()
        requestPrefs.edit { putInt("${filter}_${movieId}_movie_reviews_page", 1) }
        requestAndSaveReviews(_refreshState)
    }

}