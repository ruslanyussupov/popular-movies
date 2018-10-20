package com.example.ruslanyussupov.popularmovies;

import android.app.Application;

import com.example.ruslanyussupov.popularmovies.di.AppComponent;
import com.example.ruslanyussupov.popularmovies.di.AppModule;
import com.example.ruslanyussupov.popularmovies.di.DaggerAppComponent;

import timber.log.Timber;

public class App extends Application {

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        component = DaggerAppComponent.builder().appModule(new AppModule(this)).build();

    }

    public static AppComponent getComponent() {
        return component;
    }
}
