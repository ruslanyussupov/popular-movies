package com.example.ruslanyussupov.popularmovies

import android.app.Application

import com.example.ruslanyussupov.popularmovies.di.AppComponent
import com.example.ruslanyussupov.popularmovies.di.AppModule
import com.example.ruslanyussupov.popularmovies.di.DaggerAppComponent

import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()

    }

    companion object {
        var component: AppComponent? = null
            private set
    }
}
