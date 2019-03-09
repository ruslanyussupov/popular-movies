package com.example.ruslanyussupov.popularmovies

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ruslanyussupov.popularmovies.data.local.MovieDao
import com.example.ruslanyussupov.popularmovies.data.local.MovieDb
import com.example.ruslanyussupov.popularmovies.data.local.VideoDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoDaoTest {

    @Suppress("unused")
    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()

    private val videos = createVideos()
    private val movies = createMovies()
    private lateinit var db: MovieDb
    private lateinit var videoDao: VideoDao
    private lateinit var movieDao: MovieDao

    @Before
    fun init() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MovieDb::class.java).build()
        videoDao = db.videoDao()
        movieDao = db.movieDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testVideosReadWrite() = runBlocking {
        movieDao.insertMovies(movies)

        movies.forEach { movie ->
            videos.forEach {video ->
                video.movieId = movie.id
            }

            videoDao.insertVideos(videos)

            val movieVideos = videoDao.getVideos(movie.id)
            movieVideos.observeForever {
                assert(it == videos)
            }
        }
    }

    @Test
    fun testVideosDelete() = runBlocking {
        videoDao.deleteVideos(movies[0].id)
        val movieVideos = videoDao.getVideos(movies[0].id)
        movieVideos.observeForever {
            assert(it.isEmpty())
        }

        movieDao.deleteMovie(movies[2])
        val movieVideos2 = videoDao.getVideos(movies[2].id)
        movieVideos2.observeForever {
            assert(it.isEmpty())
        }
    }

}