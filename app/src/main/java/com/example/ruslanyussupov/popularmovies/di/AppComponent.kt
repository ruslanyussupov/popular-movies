package com.example.ruslanyussupov.popularmovies.di

import com.example.ruslanyussupov.popularmovies.data.MovieBoundaryCallback
import com.example.ruslanyussupov.popularmovies.data.Repository
import com.example.ruslanyussupov.popularmovies.data.ReviewBoundaryCallback
import com.example.ruslanyussupov.popularmovies.detail.DetailViewModel
import com.example.ruslanyussupov.popularmovies.browse.MainViewModel

import javax.inject.Singleton

import dagger.Component

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, DataModule::class])
interface AppComponent {

    fun inject(mainViewModel: MainViewModel)
    fun inject(detailViewModel: DetailViewModel)
    fun inject(repository: Repository)
    fun inject(movieBoundaryCallback: MovieBoundaryCallback)
    fun inject(reviewBoundaryCallback: ReviewBoundaryCallback)

}
