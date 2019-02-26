package com.example.ruslanyussupov.popularmovies.data.local

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.example.ruslanyussupov.popularmovies.data.model.Movie

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY indexInResponse ASC")
    fun getMovies(): DataSource.Factory<Int, Movie>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovie(id: Int): Movie?

    @Query("SELECT MAX(indexInResponse) FROM movies ")
    suspend fun getMaxIndex(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movie: List<Movie>)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("DELETE FROM movies")
    suspend fun deleteMovies()

}
