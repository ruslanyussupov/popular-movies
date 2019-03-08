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

    fun picturesPrivateStorageDir(dir: String): File? {

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

    fun saveBitmap(bitmap: Bitmap, dir: String, name: String): String? {

        val imageFile = File("${picturesPrivateStorageDir(dir).toString()}${File.separator}$name.jpg")

        if (imageFile.exists()) return imageFile.absolutePath

        val isSaved = imageFile.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, it)
        }

        Timber.d("Bitmap saved ($isSaved): ${imageFile.absolutePath}")

        return if (isSaved) imageFile.absolutePath else null

    }

    fun saveBitmap(url: String, dir: String, name: String): String? {
        val bitmap = loadBitmap(url) ?: return null
        return saveBitmap(bitmap, dir, name)
    }

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

    fun deleteFile(path: String?): Boolean {

        if (path == null || path.isEmpty()) return false

        return File(path).delete()

    }

    fun savePoster(url: String, id: Int): String? {
        return saveBitmap(url, POSTERS_DIR, "poster-$id")
    }

    fun saveBackdrop(url: String, id: Int): String? {
        return saveBitmap(url, BACKDROPS_DIR, "backdrop-$id")
    }

    fun deletePosters(ids: List<Int>) {
        val storagePath = picturesPrivateStorageDir(POSTERS_DIR)?.absolutePath ?: return
        ids.forEach {
            deleteFile("$storagePath${File.separator}poster-$it.jpg")
        }
    }

    fun deleteBackdrops(ids: List<Int>) {
        val storagePath = picturesPrivateStorageDir(BACKDROPS_DIR)?.absolutePath ?: return
        ids.forEach {
            deleteFile("$storagePath${File.separator}backdrop-$it.jpg")
        }
    }

    fun deletePoster(id: Int) {
        val storagePath = picturesPrivateStorageDir(POSTERS_DIR)?.absolutePath ?: return
        deleteFile("$storagePath${File.separator}poster-$id.jpg")
    }

    fun deleteBackdrop(id: Int) {
        val storagePath = picturesPrivateStorageDir(BACKDROPS_DIR)?.absolutePath ?: return
        deleteFile("$storagePath${File.separator}backdrop-$id.jpg")
    }

    companion object {
        private const val POSTERS_DIR = "posters"
        private const val BACKDROPS_DIR = "backdrops"
    }

}
