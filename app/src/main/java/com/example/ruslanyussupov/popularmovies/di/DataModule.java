package com.example.ruslanyussupov.popularmovies.di;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.ruslanyussupov.popularmovies.data.DataSource;
import com.example.ruslanyussupov.popularmovies.data.Repository;
import com.example.ruslanyussupov.popularmovies.data.local.FavouriteMoviesDb;
import com.example.ruslanyussupov.popularmovies.data.remote.TheMovieDbService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class DataModule {

    @Provides
    @Singleton
    public TheMovieDbService provideTheMovieDbService(Retrofit retrofit) {
        return retrofit.create(TheMovieDbService.class);
    }

    @Provides
    @Singleton
    public DataSource provideDataSource() {
        return new Repository();
    }

    @Provides
    @Singleton
    public FavouriteMoviesDb provideFavouriteMovieDb(Context appContext) {
        return Room.databaseBuilder(appContext, FavouriteMoviesDb.class,
                "fav_movies").build();
    }

}
