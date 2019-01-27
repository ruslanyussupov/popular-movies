package com.example.ruslanyussupov.popularmovies.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import com.example.ruslanyussupov.popularmovies.data.model.Movie

import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies")
    fun getFavouriteMovies(): Flowable<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getFavouriteMovie(id: Int): Single<Movie>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(movie: Movie)

    @Delete
    fun delete(movie: Movie)

    @Update
    fun update(movie: Movie)

}
