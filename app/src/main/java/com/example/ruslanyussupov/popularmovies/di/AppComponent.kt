package com.example.ruslanyussupov.popularmovies.di

import com.example.ruslanyussupov.popularmovies.data.Repository
import com.example.ruslanyussupov.popularmovies.detail.DetailViewModel
import com.example.ruslanyussupov.popularmovies.browse.MainViewModel
import com.example.ruslanyussupov.popularmovies.data.remote.MoviesRequest
import com.example.ruslanyussupov.popularmovies.data.remote.ReviewsRequest
import com.example.ruslanyussupov.popularmovies.data.remote.VideosRequest

import javax.inject.Singleton

import dagger.Component

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, DataModule::class])
interface AppComponent {

    fun inject(mainViewModel: MainViewModel)
    fun inject(detailViewModel: DetailViewModel)
    fun inject(repository: Repository)
    fun inject(moviesRequest: MoviesRequest)
    fun inject(reviewsRequest: ReviewsRequest)
    fun inject(videosRequest: VideosRequest)

}
