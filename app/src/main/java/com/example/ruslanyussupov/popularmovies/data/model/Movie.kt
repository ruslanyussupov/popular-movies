package com.example.ruslanyussupov.popularmovies.data.model


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "movies")
data class Movie(@PrimaryKey val id: Int,
                 @SerializedName("original_title")  val originalTitle: String?,
                 @SerializedName("poster_path")     val posterPath: String?,
                 @SerializedName("overview")        val overview: String?,
                 @SerializedName("vote_average")    val voteAverage: Double?,
                 @SerializedName("release_date")    val releaseDate: String?,
                 @SerializedName("backdrop_path")   val backdropPath: String?,
                 var posterLocalPath: String? = null,
                 var backdropLocalPath: String? = null) : Parcelable {

    val fullPosterPath: String
        get() = MOVIE_POSTER_MAIN_PATH + posterPath

    val fullBackdropPath: String
        get() = MOVIE_BACKDROP_MAIN_PATH + backdropPath

    companion object {
        private const val MOVIE_POSTER_MAIN_PATH = "https://image.tmdb.org/t/p/w185"
        private const val MOVIE_BACKDROP_MAIN_PATH = "https://image.tmdb.org/t/p/w780"
    }

}
