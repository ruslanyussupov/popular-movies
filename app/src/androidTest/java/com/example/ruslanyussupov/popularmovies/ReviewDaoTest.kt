package com.example.ruslanyussupov.popularmovies

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ruslanyussupov.popularmovies.data.local.MovieDao
import com.example.ruslanyussupov.popularmovies.data.local.MovieDb
import com.example.ruslanyussupov.popularmovies.data.local.ReviewDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewDaoTest {

    @Suppress("unused")
    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()

    private lateinit var db: MovieDb
    private lateinit var reviewDao: ReviewDao
    private lateinit var movieDao: MovieDao
    private val reviews = createReviews()
    private val movies = createMovies()

    @Before
    fun init() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MovieDb::class.java).build()
        reviewDao = db.reviewDao()
        movieDao = db.movieDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testReviewsReadWrite() = runBlocking {
        movieDao.insertMovies(movies)
        movies.forEach {movie ->
            reviews.forEachIndexed {index, review ->
                review.movieId = movie.id
                review.indexInResponse = index
            }
            reviewDao.insertReviews(reviews)

            val movieReviews = reviewDao.getReviews(movie.id).toLiveData(10)
            movieReviews.observeForever {
                assert(it == reviews)
            }
        }
    }

    @Test
    fun testReviewsDelete() = runBlocking {
        reviewDao.deleteReviews(movies[0].id)

        val movieReviews = reviewDao.getReviews(movies[0].id).toLiveData(10)
        movieReviews.observeForever {
            assert(it.isEmpty())
        }

        movieDao.deleteMovie(movies[2])

        val movieReviews2 = reviewDao.getReviews(movies[2].id).toLiveData(10)
        movieReviews2.observeForever {
            assert(it.isEmpty())
        }
    }

    @Test
    fun testReviewsMaxIndex() = runBlocking {
        val lastIndex = reviewDao.getMaxIndex(movies[1].id) ?: -1
        assert(lastIndex == reviews.lastIndex)
    }

}