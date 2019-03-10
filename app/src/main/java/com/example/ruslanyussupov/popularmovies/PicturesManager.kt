package com.example.ruslanyussupov.popularmovies

import android.graphics.Bitmap

interface PicturesManager {

    fun loadBitmap(url: String): Bitmap?

    fun saveBitmap(bitmap: Bitmap, dir: String, name: String): String?

    fun saveBitmap(url: String, dir: String, name: String): String?

    fun delete(dir: String, name: String): Boolean

}