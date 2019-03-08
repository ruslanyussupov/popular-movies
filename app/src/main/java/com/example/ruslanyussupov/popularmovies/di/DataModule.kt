package com.example.ruslanyussupov.popularmovies.di

import androidx.room.Room
import android.content.Context
import android.content.SharedPreferences

import com.example.ruslanyussupov.popularmovies.data.DataSource
import com.example.ruslanyussupov.popularmovies.data.Repository
import com.example.ruslanyussupov.popularmovies.data.local.MovieDb
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbService

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideTheMovieDbService(retrofit: Retrofit): TheMovieDbService {
        return retrofit.create(TheMovieDbService::class.java)
    }

    @Provides
    @Singleton
    fun provideDataSource(): DataSource {
        return Repository()
    }

    @Provides
    @Singleton
    fun provideMovieDb(appContext: Context): MovieDb {
        return Room.databaseBuilder(appContext, MovieDb::class.java,
                "movies").build()
    }

    @Provides
    @Singleton
    fun provideRequestSharedPrefs(appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("request_prefs", Context.MODE_PRIVATE)
    }

}
