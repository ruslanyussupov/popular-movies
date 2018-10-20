package com.example.ruslanyussupov.popularmovies.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.ruslanyussupov.popularmovies.data.model.Movie;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    Flowable<List<Movie>> getFavouriteMovies();

    @Query("SELECT * FROM movies WHERE id = :id")
    Single<Movie> getFavouriteMovie(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Movie movie);

    @Delete
    void delete(Movie movie);

    @Update
    void update(Movie movie);

}
