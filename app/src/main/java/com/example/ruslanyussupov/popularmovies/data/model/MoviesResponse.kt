package com.example.ruslanyussupov.popularmovies.data.model

import com.google.gson.annotations.SerializedName

data class MoviesResponse(val page: Int?,
                          @SerializedName("total_results")  val totalResults: Int?,
                          @SerializedName("total_pages")    val totalPages: Int?,
                          @SerializedName("results")        val movies: List<Movie>?)
