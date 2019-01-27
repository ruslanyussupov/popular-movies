package com.example.ruslanyussupov.popularmovies

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Environment

import java.io.File
import java.io.IOException

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber

class Utils(private val appContext: Context,
            private val okHttpClient: OkHttpClient,
            private val requestBuilder: Request.Builder) {

    // Return private external storage path
    private fun privateStorageDir(): File? {

        val imagesDir = File(appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "fav")

        if (!imagesDir.exists()) {
            if (!imagesDir.mkdirs()) {
                Timber.e("Couldn't create /fav directory.")
            }
        }

        Timber.d(imagesDir.absolutePath)
        return imagesDir

    }

    fun hasNetworkConnection(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // Save bitmap as image file in private external storage and return it's path
    fun saveBitmap(bitmap: Bitmap, name: String): String {

        val imageFile = File("${privateStorageDir().toString()}${File.separator}$name.png")

        val result = imageFile.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        Timber.d("Bitmap saved: ${imageFile.absolutePath} $result")

        return imageFile.absolutePath

    }

    fun loadBitmap(url: String): Bitmap? {
        Timber.d("Start loading bitmap $url")
        val request = requestBuilder.url(url).build()
        var response: Response? = null

        try {
            response = okHttpClient.newCall(request).execute()
        } catch (error: IOException) {
            Timber.e(error,"Couldn't fetch a bitmap from $url")
        }

        return BitmapFactory.decodeStream(response?.body()?.byteStream()) ?: null

    }

    // Delete file by it's path
    fun deleteFile(path: String?): Boolean {

        if (path == null || path.isEmpty()) return false

        return File(path).delete()

    }

}
