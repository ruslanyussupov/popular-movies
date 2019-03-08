package com.example.ruslanyussupov.popularmovies.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ruslanyussupov.popularmovies.data.model.*

@Database(entities = [Movie::class, Review::class, Video::class,
    Popular::class, TopRated::class, Favorite::class],
        version = 1, exportSchema = false)
abstract class MovieDb : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun videoDao(): VideoDao
    abstract fun reviewDao(): ReviewDao

}
