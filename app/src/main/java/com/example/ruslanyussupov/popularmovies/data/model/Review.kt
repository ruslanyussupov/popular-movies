package com.example.ruslanyussupov.popularmovies.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Review(val author: String,
                  val content: String,
                  val url: String) : Parcelable
