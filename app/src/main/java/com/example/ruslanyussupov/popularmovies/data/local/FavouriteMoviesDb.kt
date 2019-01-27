package com.example.ruslanyussupov.popularmovies.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

import com.example.ruslanyussupov.popularmovies.data.model.Movie

@Database(entities = [Movie::class], version = 1, exportSchema = false)
abstract class FavouriteMoviesDb : RoomDatabase() {

    abstract fun movieDao(): MovieDao

}
