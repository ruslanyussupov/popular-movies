package com.example.ruslanyussupov.popularmovies.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.ruslanyussupov.popularmovies.data.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class FavouriteMoviesDb extends RoomDatabase {

    public abstract MovieDao movieDao();

}
