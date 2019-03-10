package com.example.ruslanyussupov.popularmovies

import android.content.Context
import android.net.ConnectivityManager


class Utils(private val appContext: Context) {

    fun hasNetworkConnection(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}
