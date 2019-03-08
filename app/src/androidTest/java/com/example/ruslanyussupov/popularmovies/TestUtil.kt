package com.example.ruslanyussupov.popularmovies

import com.example.ruslanyussupov.popularmovies.data.model.Movie
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

fun createMovies(): List<Movie> {

    return listOf(
            Movie(399579,
                    "Alita: Battle Angel",
                    "/xRWht48C2V8XNfzvPehyClOvDni.jpg",
                    "When Alita awakens with no memory of who she is in a future world...",
                    8.5,
                    "2019-01-31",
                    "/aQXTw3wIWuFMy0beXRiZ1xVKtcf.jpg",
                    "/cache/posters/poster-399579.jpg",
                    "cache/backdrops/backdrop-399579.jpg"),
            Movie(0,
                    null,
                    null,
                    null,
                    0.0,
                    null,
                    null),
            Movie(450465,
                    "Glass",
                    "/svIDTNUoajS8dLEo7EosxvyAsgJ.jpg",
                    "In a series of escalating encounters, security guard David Dunn uses his...",
                    6.6,
                    "2019-01-16",
                    "/lvjscO8wmpEbIfOEZi92Je8Ktlg.jpg")
    )

}

fun createOkHttpClient(): OkHttpClient {
    val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Timber.d("API: $it") })
    logger.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor { chain ->
                val request = chain.request()

                val url = request.url().newBuilder()
                        .addQueryParameter("api_key", BuildConfig.THEMOVIEDB_API_KEY)
                        .addQueryParameter("language", "en-US")
                        .build()

                chain.proceed(request.newBuilder().url(url).build())
            }.build()
}