package com.example.ruslanyussupov.popularmovies.di


import com.example.ruslanyussupov.popularmovies.BuildConfig
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbService


import javax.inject.Named
import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    @Singleton
    fun providesRequestBuilder(): Request.Builder {
        return Request.Builder()
    }

    @Provides
    @Singleton
    @Named(API_CLIENT)
    fun providesApiClient(): OkHttpClient {
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

    @Provides
    @Singleton
    fun providesRetrofit(@Named(API_CLIENT) client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(TheMovieDbService.ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    companion object {
        private const val API_CLIENT = "API_CLIENT"
    }

}
