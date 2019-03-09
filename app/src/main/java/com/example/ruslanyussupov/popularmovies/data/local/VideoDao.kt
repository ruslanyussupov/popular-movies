package com.example.ruslanyussupov.popularmovies.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ruslanyussupov.popularmovies.data.model.Video

@Dao
interface VideoDao {

    @Query("SELECT * FROM videos WHERE :movieId = movieId")
    fun getVideos(movieId: Int): LiveData<List<Video>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<Video>)

    @Query("DELETE FROM videos WHERE :movieId = movieId")
    suspend fun deleteVideos(movieId: Int)

}