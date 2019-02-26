package com.example.ruslanyussupov.popularmovies.data.model


import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "videos",
        foreignKeys = [ForeignKey(
                entity = Movie::class,
                parentColumns = ["id"],
                childColumns = ["movieId"],
                onDelete = ForeignKey.CASCADE)
        ])
class Video(@PrimaryKey val id: String,
            val key: String,
            val name: String,
            val site: String,
            val size: Int,
            @ColumnInfo(index = true) var movieId: Int? = null) : Parcelable {

    fun previewImageUrl() = "https://img.youtube.com/vi/$key/1.jpg"

    fun url() = "https://www.youtube.com/watch?v=$key"

}
