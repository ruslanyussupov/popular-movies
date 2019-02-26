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
import javax.inject.Named

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
    @Named(value = "FavouriteDb")
    fun provideFavouriteMovieDb(appContext: Context): MovieDb {
        return Room.databaseBuilder(appContext, MovieDb::class.java,
                "fav_movies").build()
    }

    @Provides
    @Singleton
    @Named(value = "TopRatedDb")
    fun provideTopRatedMovieDb(appContext: Context): MovieDb {
        return Room.databaseBuilder(appContext, MovieDb::class.java,
                "top_rated_movies").build()
    }

    @Provides
    @Singleton
    @Named(value = "PopularDb")
    fun providePopularMoviesDb(appContext: Context): MovieDb {
        return Room.databaseBuilder(appContext, MovieDb::class.java,
                "popular_movies").build()
    }

    @Provides
    @Singleton
    fun provideRequestSharedPrefs(appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("request_prefs", Context.MODE_PRIVATE)
    }

}
