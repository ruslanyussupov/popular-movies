package com.example.ruslanyussupov.popularmovies.di;

import android.content.Context;

import com.example.ruslanyussupov.popularmovies.Utils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@Module
public class AppModule {

    private Context appContext;

    public AppModule(Context appContext) {
        this.appContext = appContext;
    }

    @Provides
    @Singleton
    public Context providesAppContext() {
        return appContext;
    }

    @Provides
    @Singleton
    public Utils providesUtils(OkHttpClient okHttpClient, Request.Builder requestBuilder) {
        return new Utils(appContext, okHttpClient, requestBuilder);
    }

}
