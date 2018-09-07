package com.example.ruslanyussupov.popularmovies.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.ruslanyussupov.popularmovies.data.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getFavouriteMovies();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Movie movie);

    @Delete
    void delete(Movie movie);

}
