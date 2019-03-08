package com.example.ruslanyussupov.popularmovies.data.model

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "reviews",
        foreignKeys = [ForeignKey(
                entity = Movie::class,
                parentColumns = ["id"],
                childColumns = ["movieId"],
                onDelete = ForeignKey.CASCADE)
        ])
data class Review(@PrimaryKey val id: String,
                  val author: String?,
                  val content: String?,
                  val url: String?,
                  @ColumnInfo(index = true) var movieId: Int? = null) : Parcelable {

        @IgnoredOnParcel
        var indexInResponse = -1
}

