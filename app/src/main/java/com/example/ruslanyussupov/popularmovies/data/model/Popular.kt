package com.example.ruslanyussupov.popularmovies.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "popular",
        foreignKeys = [ForeignKey(
                entity = Movie::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("movieId"),
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.NO_ACTION)
        ])
data class Popular(@PrimaryKey
                   val movieId: Int,
                   var indexInResponse: Int = -1)