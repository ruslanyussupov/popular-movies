package com.example.ruslanyussupov.popularmovies

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PicturesManagerTest {

    private lateinit var picturesManager: PicturesManager
    private lateinit var context: Context

    @Before
    fun init() {
        context = ApplicationProvider.getApplicationContext<Context>()
        picturesManager = PicturesManagerImpl(context)
    }

    @Test
    fun testPictureReadWrite() {
        val picturePath = picturesManager.saveBitmap(
                "https://image.tmdb.org/t/p/w185/xRWht48C2V8XNfzvPehyClOvDni.jpg",
                "posters",
                "poster-1")
        Assert.assertNotNull(picturePath)
    }

    @Test
    fun testPictureDelete() {
        assert(picturesManager.delete("posters", "poster-1"))
    }

}