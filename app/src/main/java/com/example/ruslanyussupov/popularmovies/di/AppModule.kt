package com.example.ruslanyussupov.popularmovies.di

import android.content.Context

import com.example.ruslanyussupov.popularmovies.Utils

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request

@Module
class AppModule(private val appContext: Context) {

    @Provides
    @Singleton
    fun providesAppContext(): Context {
        return appContext
    }

    @Provides
    @Singleton
    fun providesUtils(okHttpClient: OkHttpClient, requestBuilder: Request.Builder): Utils {
        return Utils(appContext, okHttpClient, requestBuilder)
    }

}
