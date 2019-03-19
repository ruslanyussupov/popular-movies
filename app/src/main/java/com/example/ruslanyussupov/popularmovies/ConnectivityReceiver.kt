package com.example.ruslanyussupov.popularmovies

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class ConnectivityReceiver : BroadcastReceiver() {

    lateinit var connectivityReceiverListener: ConnectivityReceiverListener

    override fun onReceive(context: Context?, intent: Intent?) {

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager? ?: return
        val isConnected = connectivityManager.activeNetworkInfo?.isConnected ?: false

        connectivityReceiverListener.onNetworkConnectionChanged(isConnected)

    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

}