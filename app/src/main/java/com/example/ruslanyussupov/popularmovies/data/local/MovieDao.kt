package com.example.ruslanyussupov.popularmovies.data.local

import androidx.paging.DataSource
import androidx.room.*
import com.example.ruslanyussupov.popularmovies.data.model.*

@Dao
abstract class MovieDao {

    @Query("SELECT id, originalTitle, posterPath, overview, voteAverage, releaseDate, backdropPath, posterLocalPath, backdropLocalPath FROM popular INNER JOIN movies ON popular.movieId = movies.id ORDER BY indexInResponse ASC")
    abstract fun getPopularMovies(): DataSource.Factory<Int, Movie>

    @Query("SELECT id, originalTitle, posterPath, overview, voteAverage, releaseDate, backdropPath, posterLocalPath, backdropLocalPath FROM top_rated INNER JOIN movies ON top_rated.movieId = movies.id ORDER BY indexInResponse ASC")
    abstract fun getTopRatedMovies(): DataSource.Factory<Int, Movie>

    @Query("SELECT id, originalTitle, posterPath, overview, voteAverage, releaseDate, backdropPath, posterLocalPath, backdropLocalPath FROM favorite INNER JOIN movies ON favorite.movieId = movies.id ORDER BY indexInResponse ASC")
    abstract fun getFavoriteMovies(): DataSource.Factory<Int, Movie>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    abstract suspend fun getMovie(movieId: Int): Movie?

    @Query("SELECT * FROM popular WHERE movieId = :movieId")
    abstract suspend fun getPopular(movieId: Int): Popular?

    @Query("SELECT * FROM top_rated WHERE movieId = :movieId")
    abstract suspend fun getTopRated(movieId: Int): TopRated?

    @Query("SELECT * FROM favorite WHERE movieId = :movieId")
    abstract suspend fun getFavorite(movieId: Int): Favorite?

    @Query("SELECT * FROM popular")
    abstract suspend fun getPopulars(): List<Popular>

    @Query("SELECT * FROM top_rated")
    abstract suspend fun getTopRated(): List<TopRated>

    @Query("SELECT * FROM favorite")
    abstract suspend fun getFavorites(): List<Favorite>

    @Query("SELECT MAX(indexInResponse) FROM popular")
    abstract suspend fun getPopularMaxIndex(): Int?

    @Query("SELECT MAX(indexInResponse) FROM top_rated")
    abstract suspend fun getTopRatedMaxIndex(): Int?

    @Query("SELECT MAX(indexInResponse) FROM favorite")
    abstract suspend fun getFavoriteMaxIndex(): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertMovies(movie: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertPopulars(populars: List<Popular>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertTopRated(topRated: List<TopRated>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertFavorites(favorites: List<Favorite>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertFavorite(favorite: Favorite)

    @Delete
    abstract suspend fun deleteMovie(movie: Movie)

    @Query("DELETE FROM movies WHERE id IN(:movieIds)")
    abstract suspend fun deleteMovies(movieIds: List<Int>)

    @Query("DELETE FROM favorite WHERE movieId = :movieId")
    abstract suspend fun deleteFavorite(movieId: Int)

    @Query("DELETE FROM favorite")
    abstract suspend fun deleteFavorites()

    @Query("DELETE FROM popular")
    abstract suspend fun deletePopulars()

    @Query("DELETE FROM top_rated")
    abstract suspend fun deleteTopRated()

    @Query("SELECT id FROM movies WHERE id IN(SELECT movieId FROM popular WHERE movieId NOT IN(SELECT movieId FROM top_rated UNION SELECT movieId FROM favorite))")
    abstract suspend fun getPopularMoviesIdsOnly(): List<Int>

    @Query("SELECT id FROM movies WHERE id IN(SELECT movieId FROM top_rated WHERE movieId NOT IN(SELECT movieId FROM popular UNION SELECT movieId FROM favorite))")
    abstract suspend fun getTopRatedMoviesIdsOnly(): List<Int>

    @Query("SELECT id FROM movies WHERE id IN(SELECT movieId FROM favorite WHERE movieId NOT IN(SELECT movieId FROM popular UNION SELECT movieId FROM top_rated))")
    abstract suspend fun getFavoriteMoviesIdsOnly(): List<Int>

    suspend fun insertPopularMovies(movies: List<Movie>) {
        var lastIndex = getPopularMaxIndex() ?: -1
        val populars = movies.map { Popular(it.id, ++lastIndex) }
        insertMovies(movies)
        insertPopulars(populars)
    }

    suspend fun insertTopRatedMovies(movies: List<Movie>) {
        var lastIndex = getTopRatedMaxIndex() ?: -1
        val topRated = movies.map { TopRated(it.id, ++lastIndex) }
        insertMovies(movies)
        insertTopRated(topRated)
    }

    suspend fun insertFavoriteMovies(movies: List<Movie>) {
        var lastIndex = getFavoriteMaxIndex() ?: -1
        val favorites = movies.map { Favorite(it.id, ++lastIndex) }
        insertMovies(movies)
        insertFavorites(favorites)
    }

    suspend fun insertFavoriteMovie(movie: Movie) {
        val lastIndex = getFavoriteMaxIndex() ?: -1
        insertMovie(movie)
        insertFavorite(Favorite(movie.id, lastIndex + 1))
    }

    suspend fun deletePopularMovies(): List<Int> {
        val ids = getPopularMoviesIdsOnly()
        deletePopulars()
        deleteMovies(ids)
        return ids
    }

    suspend fun deleteTopRatedMovies(): List<Int> {
        val ids = getTopRatedMoviesIdsOnly()
        deleteTopRated()
        deleteMovies(ids)
        return ids
    }

    suspend fun deleteFavoriteMovies(): List<Int> {
        val ids = getFavoriteMoviesIdsOnly()
        deleteFavorites()
        deleteMovies(ids)
        return ids
    }

    suspend fun deleteFavoriteMovie(movie: Movie): Int {
        deleteFavorite(movie.id)
        val popular = getPopular(movie.id)
        val topRated = getTopRated(movie.id)
        if (popular == null && topRated == null) {
            deleteMovie(movie)
            return movie.id
        }
        return -1
    }

}
