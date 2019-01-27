package com.example.ruslanyussupov.popularmovies.data.model


import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Video(val key: String,
            val name: String,
            val site: String,
            val size: Int) : Parcelable {

    @IgnoredOnParcel
    val previewImagePath: String
        get() = "https://img.youtube.com/vi/$key/1.jpg"

    @IgnoredOnParcel
    val url: String
        get() = "https://www.youtube.com/watch?v=$key"

}
