package com.example.ruslanyussupov.popularmovies.data

import androidx.lifecycle.LiveData

data class Listing<T>(
        val data: LiveData<T>,
        val networkState: LiveData<NetworkState>?,
        val refresh: (() -> Unit)?,
        val refreshState: LiveData<NetworkState>?,
        val retry: (() -> Unit)?)