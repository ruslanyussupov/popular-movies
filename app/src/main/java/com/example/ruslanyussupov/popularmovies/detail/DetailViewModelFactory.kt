package com.example.ruslanyussupov.popularmovies.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.ruslanyussupov.popularmovies.data.model.Movie

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(private val movie: Movie) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(movie) as T
        }
        throw IllegalArgumentException("modelClass should be instance of DetailViewModel.class")
    }
}
