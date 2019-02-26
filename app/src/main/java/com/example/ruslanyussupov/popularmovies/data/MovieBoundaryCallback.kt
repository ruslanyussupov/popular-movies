package com.example.ruslanyussupov.popularmovies.data

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.Utils
import com.example.ruslanyussupov.popularmovies.data.DataSource.Filter
import com.example.ruslanyussupov.popularmovies.data.local.MovieDb
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.data.model.MoviesResponse
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class MovieBoundaryCallback(private val filter: Filter,
                            private val db: MovieDb,
                            private val movieDbAPI: TheMovieDbService,
                            private val utils: Utils) : PagedList.BoundaryCallback<Movie>() {

    @Inject
    internal lateinit var requestPrefs: SharedPreferences

    private var isRequestInProgress = false
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState
    private val _refreshState = MutableLiveData<NetworkState>()
    val refreshState: LiveData<NetworkState> = _refreshState
    val retry = { requestAndSaveData(_networkState) }

    init {
        App.component?.inject(this)
    }

    override fun onZeroItemsLoaded() {
        Timber.d("$filter onZeroItemsLoaded")
        requestAndSaveData(_networkState)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Movie) {
        Timber.d("$filter onItemAtEndLoaded")
        requestAndSaveData(_networkState)
    }

    private fun requestAndSaveData(networkState: MutableLiveData<NetworkState>) {

        Timber.d("Request in progress: $isRequestInProgress")

        if (isRequestInProgress) return

        if (filter == Filter.POPULAR) {
            requestPopularMovies(networkState)
        }
        else if (filter == Filter.TOP_RATED) {
            requestTopRatedMovies(networkState)
        }



    }

    private fun requestPopularMovies(networkState: MutableLiveData<NetworkState>) {
        networkState.postValue(NetworkState.LOADING)
        isRequestInProgress = true
        val page = requestPrefs.getInt("${filter}_movies_page", 1)
        movieDbAPI.getPopularMovies(page).enqueue(object : Callback<MoviesResponse> {
            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                Timber.e(t)
                networkState.postValue(NetworkState.error(t.message))
                isRequestInProgress = false
            }

            override fun onResponse(call: Call<MoviesResponse>, response: Response<MoviesResponse>) {
                val moviesResponse = response.body()
                Timber.d("Movies response: page: $page ${moviesResponse?.movies?.size}, ${moviesResponse?.totalPages}, ${moviesResponse?.totalResults}")
                if (moviesResponse == null) {
                    Timber.e("Popular movies response is null.")
                    networkState.postValue(NetworkState.error("Popular movies response is null."))
                    isRequestInProgress = false
                } else {
                    GlobalScope.launch {
                        saveMovies(moviesResponse.movies)
                        requestPrefs.edit { putInt("${filter}_movies_page", page + 1) }
                        networkState.postValue(NetworkState.LOADED)
                        isRequestInProgress = false
                    }
                }
            }

        })
    }

    private fun requestTopRatedMovies(networkState: MutableLiveData<NetworkState>) {
        networkState.postValue(NetworkState.LOADING)
        isRequestInProgress = true
        val page = requestPrefs.getInt("${filter}_movies_page", 1)
        movieDbAPI.getTopRatedMovies(page).enqueue(object : Callback<MoviesResponse> {
            override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                Timber.e(t)
                networkState.postValue(NetworkState.error(t.message))
                isRequestInProgress = false
            }

            override fun onResponse(call: Call<MoviesResponse>, response: Response<MoviesResponse>) {
                val moviesResponse = response.body()
                if (moviesResponse == null) {
                    Timber.e("Top rated movies response is null.")
                    networkState.postValue(NetworkState.error("Top rated movies response is null."))
                    isRequestInProgress = false
                } else {
                    GlobalScope.launch {
                        saveMovies(moviesResponse.movies)
                        if (!moviesResponse.movies.isNullOrEmpty()) {
                            requestPrefs.edit { putInt("${filter}_movies_page", page + 1) }
                        }
                        networkState.postValue(NetworkState.LOADED)
                        isRequestInProgress = false
                    }
                }
            }

        })
    }

    suspend fun saveMovies(movies: List<Movie>) {
        val jobs = mutableListOf<Job>()
        var index = db.movieDao().getMaxIndex() ?: -1
        movies.onEach {
            jobs += GlobalScope.launch {
                it.posterLocalPath = utils.saveBitmap(it.fullPosterPath, filter.name.toLowerCase(),"poster-${it.id}")
                it.backdropLocalPath = utils.saveBitmap(it.fullBackdropPath, filter.name.toLowerCase(), "backdrop-${it.id}")
            }
        }
        jobs.forEach { it.join() }
        movies.onEach {
            it.indexInResponse = ++index
        }
        db.movieDao().insertMovies(movies)
    }

    fun refresh() = runBlocking {

        GlobalScope.launch {
            db.movieDao().deleteMovies()
        }.join()
        requestPrefs.edit { putInt("${filter}_movies_page", 1) }
        requestAndSaveData(_refreshState)

    }

}