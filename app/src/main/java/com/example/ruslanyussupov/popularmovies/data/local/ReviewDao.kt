package com.example.ruslanyussupov.popularmovies.data.local

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ruslanyussupov.popularmovies.data.model.Review

@Dao
interface ReviewDao {

    @Query("SELECT * FROM reviews WHERE :movieId = movieId ORDER BY indexInResponse ASC")
    fun getReviews(movieId: Int): DataSource.Factory<Int, Review>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReviews(reviews: List<Review>)

    @Query("DELETE FROM reviews WHERE :movieId = movieId")
    suspend fun deleteReviews(movieId: Int)

    @Query("SELECT MAX(indexInResponse) FROM reviews ")
    suspend fun getMaxIndex(): Int?

}