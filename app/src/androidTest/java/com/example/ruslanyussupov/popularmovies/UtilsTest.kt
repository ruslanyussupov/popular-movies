package com.example.ruslanyussupov.popularmovies

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.Request
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsTest {

    private lateinit var utils: Utils
    private lateinit var context: Context

    @Before
    fun createUtils() {
        context = ApplicationProvider.getApplicationContext<Context>()
        val okHttp = createOkHttpClient()
        val requestBuilder = Request.Builder()
        utils = Utils(context, okHttp, requestBuilder)
    }

    @Test
    fun bitmapReadWrite() {
        val imagePath = utils.saveBitmap("https://image.tmdb.org/t/p/w185/xRWht48C2V8XNfzvPehyClOvDni.jpg",
                "test",
                "test")
        val externalPath = utils.picturesPrivateStorageDir("test")
        assert(imagePath == "$externalPath/test/test.jpg")
        assert(utils.deleteFile(imagePath))
    }

}