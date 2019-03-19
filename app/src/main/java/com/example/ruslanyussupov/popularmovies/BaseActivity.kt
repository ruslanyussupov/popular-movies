package com.example.ruslanyussupov.popularmovies

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private val connectivityReceiver = ConnectivityReceiver()
    private var noConnectionSnackbar: Snackbar? = null
    private var backOnlineSnackbar: Snackbar? = null

    override fun onStart() {
        super.onStart()
        connectivityReceiver.connectivityReceiverListener = this
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectivityReceiver)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            showBackOnlineSnackbar()
        } else {
            showNoConnectionSnackbar()
        }
    }

    private fun showNoConnectionSnackbar() {
        if (noConnectionSnackbar == null) {
            noConnectionSnackbar = Snackbar.make(findViewById(android.R.id.content),
                    "No connection", Snackbar.LENGTH_INDEFINITE)
        }
        noConnectionSnackbar?.show()
    }

    private fun showBackOnlineSnackbar() {
        if (noConnectionSnackbar?.isShown == true) {
            noConnectionSnackbar?.dismiss()
            if (backOnlineSnackbar == null) {
                backOnlineSnackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Back online", Snackbar.LENGTH_SHORT)
            }
            backOnlineSnackbar?.show()
        }

    }

}