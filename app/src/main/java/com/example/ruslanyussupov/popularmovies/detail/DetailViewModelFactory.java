package com.example.ruslanyussupov.popularmovies.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.ruslanyussupov.popularmovies.data.model.Movie;

public class DetailViewModelFactory implements ViewModelProvider.Factory {

    private final Movie movie;

    public DetailViewModelFactory(Movie movie) {
        this.movie = movie;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            return (T) new DetailViewModel(movie);
        }
        throw new IllegalArgumentException("modelClass should be instance of DetailViewModel.class");
    }
}
