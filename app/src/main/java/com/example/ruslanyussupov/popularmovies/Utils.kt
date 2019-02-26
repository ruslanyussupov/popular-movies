package com.example.ruslanyussupov.popularmovies

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Environment
import androidx.annotation.WorkerThread

import java.io.File
import java.io.IOException

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber

class Utils(private val appContext: Context,
            private val okHttpClient: OkHttpClient,
            private val requestBuilder: Request.Builder) {

    /**
     * Return private external storage path
     */
    private fun privateStorageDir(dir: String): File? {

        val imagesDir = File(appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                dir)

        if (!imagesDir.exists()) {
            if (!imagesDir.mkdirs()) {
                Timber.e("Couldn't create /fav directory.")
            }
        }

        Timber.d("Favourite movies pictures storage path: ${imagesDir.absolutePath}")
        return imagesDir

    }

    fun hasNetworkConnection(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    /**
     * Save bitmap as image file in private external storage and return it's path
     */
    @WorkerThread
    fun saveBitmap(bitmap: Bitmap, dir: String, name: String): String {

        val imageFile = File("${privateStorageDir(dir).toString()}${File.separator}$name.jpg")

        val isSaved = imageFile.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it)
        }

        Timber.d("Bitmap saved ($isSaved): ${imageFile.absolutePath}")

        return if (isSaved) imageFile.absolutePath else ""

    }

    @WorkerThread
    fun saveBitmap(url: String, dir: String, name: String): String {
        val bitmap = loadBitmap(url) ?: return ""
        return saveBitmap(bitmap, dir, name)
    }

    @WorkerThread
    fun loadBitmap(url: String): Bitmap? {
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

    @WorkerThread
    fun deleteFile(path: String?): Boolean {

        if (path == null || path.isEmpty()) return false

        return File(path).delete()

    }

}
