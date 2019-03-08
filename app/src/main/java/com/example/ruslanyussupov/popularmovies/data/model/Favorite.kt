package com.example.ruslanyussupov.popularmovies.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "favorite",
        foreignKeys = [ForeignKey(
                entity = Movie::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("movieId"),
                onDelete = ForeignKey.RESTRICT,
                onUpdate = ForeignKey.NO_ACTION)
        ])
class Favorite(@PrimaryKey
               val movieId: Int,
               val indexInResponse: Int = -1)