package com.example.ruslanyussupov.popularmovies.di

import android.content.Context
import com.example.ruslanyussupov.popularmovies.PicturesManager
import com.example.ruslanyussupov.popularmovies.PicturesManagerImpl

import javax.inject.Singleton

import dagger.Module
import dagger.Provides


@Module
class AppModule(private val appContext: Context) {

    @Provides
    @Singleton
    fun providesAppContext(): Context {
        return appContext
    }

    @Provides
    @Singleton
    fun providesPicturesManager(appContext: Context): PicturesManager {
        return PicturesManagerImpl(appContext)
    }

}
