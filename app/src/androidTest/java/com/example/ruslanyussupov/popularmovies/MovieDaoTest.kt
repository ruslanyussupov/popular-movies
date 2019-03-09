package com.example.ruslanyussupov.popularmovies

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ruslanyussupov.popularmovies.data.local.MovieDao
import com.example.ruslanyussupov.popularmovies.data.local.MovieDb
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    @Suppress("unused")
    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()

    private lateinit var db: MovieDb
    private lateinit var movieDao: MovieDao
    private val movies = createMovies()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MovieDb::class.java).build()
        movieDao = db.movieDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testPopularMoviesReadWrite() = runBlocking {
        movieDao.insertPopularMovies(movies)
        val popularMovies = movieDao.getPopularMovies().toLiveData(10)

        popularMovies.observeForever {
            assert(it == movies)
        }
    }

    @Test
    fun testPopularMoviesDelete() = runBlocking {
        movieDao.deletePopularMovies()
        movieDao.deletePopulars()
        val populars = movieDao.getPopulars()
        assert(populars.isEmpty())
        val popularMovies = movieDao.getPopularMovies().toLiveData(10)

        popularMovies.observeForever {
            assert(it.isEmpty())
        }
    }

    @Test
    fun testTopRatedMoviesReadWrite() = runBlocking {
        movieDao.insertTopRatedMovies(movies)
        val topRatedMovies = movieDao.getTopRatedMovies().toLiveData(10)

        topRatedMovies.observeForever {
            assert(it == movies)
        }
    }

    @Test
    fun testTopRatedMoviesDelete() = runBlocking {
        movieDao.deleteTopRatedMovies()
        val topRated = movieDao.getTopRated()
        assert(topRated.isEmpty())
        val topRatedMovies = movieDao.getTopRatedMovies().toLiveData(10)

        topRatedMovies.observeForever {
            assert(it.isEmpty())
        }
    }

    @Test
    fun testFavoriteMoviesReadWrite() = runBlocking {
        movieDao.insertFavoriteMovies(movies)
        val favoriteMovies = movieDao.getFavoriteMovies().toLiveData(10)

        favoriteMovies.observeForever {
            assert(it == movies)
        }
    }

    @Test
    fun testFavoriteMoviesDelete() = runBlocking {
        movieDao.deleteFavoriteMovies()
        val favorites = movieDao.getFavorites()
        assert(favorites.isEmpty())
        val favoriteMovies = movieDao.getFavoriteMovies().toLiveData(10)

        favoriteMovies.observeForever {
            assert(it.isEmpty())
        }
    }

    @Test
    fun testFavoriteMovieReadWrite() = runBlocking {
        movies.forEach {
            movieDao.insertFavoriteMovie(it)
            val favorite = movieDao.getFavorite(it.id)
            Assert.assertNotNull(favorite)
            val favoriteMovie = movieDao.getMovie(it.id)
            assert(favoriteMovie == it)
        }
    }

    @Test
    fun testFavoriteMovieDelete() = runBlocking {
        movies.forEach {
            movieDao.deleteFavoriteMovie(it)
            val favorite = movieDao.getFavorite(it.id)
            assert(favorite == null)
            val movie = movieDao.getMovie(it.id)
            assert(movie == null)
        }
    }

    @Test
    fun testPopularMoviesDeleteWithConflict() = runBlocking {
        movieDao.insertPopularMovies(movies)
        movieDao.insertTopRatedMovies(movies)
        movieDao.deletePopularMovies()
        val populars = movieDao.getPopulars()
        assert(populars.isEmpty())
        val topRatedMovies = movieDao.getTopRatedMovies().toLiveData(10)

        topRatedMovies.observeForever {
            assert(it == movies)
        }

        movieDao.deleteTopRatedMovies()

        movieDao.insertPopularMovies(movies)
        movieDao.insertFavoriteMovies(movies)
        movieDao.deletePopularMovies()
        val populars2 = movieDao.getPopulars()
        assert(populars2.isEmpty())
        val favoriteMovies = movieDao.getFavoriteMovies().toLiveData(10)

        favoriteMovies.observeForever {
            assert(it == movies)
        }

        movieDao.deleteFavoriteMovies()

        Unit
    }

    @Test
    fun testTopRatedMoviesDeleteOnConflict() = runBlocking {
        movieDao.insertTopRatedMovies(movies)
        movieDao.insertPopularMovies(movies)
        movieDao.deleteTopRatedMovies()
        val topRated = movieDao.getTopRated()
        assert(topRated.isEmpty())
        val popularMovies = movieDao.getPopularMovies().toLiveData(10)

        popularMovies.observeForever {
            assert(it == movies)
        }

        movieDao.deletePopularMovies()

        movieDao.insertTopRatedMovies(movies)
        movieDao.insertFavoriteMovies(movies)
        movieDao.deleteTopRatedMovies()
        val topRated2 = movieDao.getTopRated()
        assert(topRated2.isEmpty())
        val favoriteMovies = movieDao.getFavoriteMovies().toLiveData(10)

        favoriteMovies.observeForever {
            assert(it == movies)
        }

        movieDao.deleteFavoriteMovies()

        Unit
    }

    @Test
    fun testFavoriteMoviesDeleteOnConflict() = runBlocking {
        movieDao.insertFavoriteMovies(movies)
        movieDao.insertPopularMovies(movies)
        movieDao.deleteFavoriteMovies()
        val favorites = movieDao.getFavorites()
        assert(favorites.isEmpty())
        val popularMovies = movieDao.getPopularMovies().toLiveData(10)

        popularMovies.observeForever {
            assert(it == movies)
        }

        movieDao.deletePopularMovies()

        movieDao.insertFavoriteMovies(movies)
        movieDao.insertTopRatedMovies(movies)
        movieDao.deleteFavoriteMovies()
        val favorites2 = movieDao.getFavorites()
        assert(favorites2.isEmpty())
        val topRatedMovies = movieDao.getTopRatedMovies().toLiveData(10)

        topRatedMovies.observeForever {
            assert(it == movies)
        }

        movieDao.deleteTopRatedMovies()

        Unit
    }

    @Test
    fun testFavoriteMovieDeleteOnConflict() = runBlocking {
        movieDao.insertFavoriteMovies(movies)
        movieDao.insertPopularMovies(movies)
        movies.forEach {
            movieDao.deleteFavoriteMovie(it)
            val favorite = movieDao.getFavorite(it.id)
            assert(favorite == null)
            val movie = movieDao.getMovie(it.id)
            assert(movie == it)
        }

        movieDao.deletePopularMovies()

        movieDao.insertFavoriteMovies(movies)
        movieDao.insertTopRatedMovies(movies)
        movies.forEach {
            movieDao.deleteFavoriteMovie(it)
            val favorite = movieDao.getFavorite(it.id)
            assert(favorite == null)
            val movie = movieDao.getMovie(it.id)
            assert(movie == it)
        }

        movieDao.deleteTopRatedMovies()

        Unit
    }

    @Test
    fun testPopularsLastIndex() = runBlocking {
        movieDao.insertPopularMovies(movies)
        val lastIndex = movieDao.getPopularMaxIndex()
        assert(lastIndex == movies.lastIndex)
        movieDao.deletePopularMovies()

        Unit
    }

    @Test
    fun testTopRatedLastIndex() = runBlocking {
        movieDao.insertTopRatedMovies(movies)
        val lastIndex = movieDao.getTopRatedMaxIndex()
        assert(lastIndex == movies.lastIndex)
        movieDao.deleteTopRatedMovies()

        Unit
    }

    @Test
    fun favoriteLastIndex() = runBlocking {
        movieDao.insertFavoriteMovies(movies)
        val lastIndex = movieDao.getFavoriteMaxIndex()
        assert(lastIndex == movies.lastIndex)
        movieDao.deleteFavoriteMovies()

        Unit
    }

}