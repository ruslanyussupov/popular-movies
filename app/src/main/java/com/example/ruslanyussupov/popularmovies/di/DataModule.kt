package com.example.ruslanyussupov.popularmovies.di

import androidx.room.Room
import android.content.Context

import com.example.ruslanyussupov.popularmovies.data.DataSource
import com.example.ruslanyussupov.popularmovies.data.Repository
import com.example.ruslanyussupov.popularmovies.data.local.FavouriteMoviesDb
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
    fun provideFavouriteMovieDb(appContext: Context): FavouriteMoviesDb {
        return Room.databaseBuilder(appContext, FavouriteMoviesDb::class.java,
                "fav_movies").build()
    }

}
