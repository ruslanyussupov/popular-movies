package com.example.ruslanyussupov.popularmovies

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.File
import java.io.IOException

class PicturesManagerImpl(private val appContext: Context) : PicturesManager {

    private val okHttpClient = createClient()

    private val requestBuilder = Request.Builder()

    override fun loadBitmap(url: String): Bitmap? {
        Timber.d("Start loading bitmap $url")
        val request = requestBuilder.url(url).build()
        val response: Response?

        try {
            response = okHttpClient.newCall(request).execute()
        } catch (error: IOException) {
            Timber.e(error,"Couldn't fetch a bitmap through $url")
            return null
        }

        return BitmapFactory.decodeStream(response?.body()?.byteStream()) ?: null
    }

    override fun saveBitmap(bitmap: Bitmap, dir: String, name: String): String? {
        val picture = File("${getDir(dir).toString()}${File.separator}$name.jpg")

        if (picture.exists()) return picture.absolutePath

        val isSaved = picture.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, it)
        }

        Timber.d("Bitmap saved ($isSaved): ${picture.absolutePath}")

        return if (isSaved) picture.absolutePath else null
    }

    override fun saveBitmap(url: String, dir: String, name: String): String? {
        val bitmap = loadBitmap(url) ?: return null
        return saveBitmap(bitmap, dir, name)
    }

    override fun delete(dir: String, name: String): Boolean {
        return File(getDir(dir), "$name.jpg").delete()
    }

    private fun getDir(dir: String): File? {

        val picturesDir = File(appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), dir)

        if (!picturesDir.exists()) {
            if (!picturesDir.mkdirs()) {
                Timber.e("Couldn't create /$dir directory.")
            }
        }

        return picturesDir

    }

    private fun createClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Timber.d("Downloading picture: $it")
        })
        logger.level = HttpLoggingInterceptor.Level.BASIC
        return OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
    }

}