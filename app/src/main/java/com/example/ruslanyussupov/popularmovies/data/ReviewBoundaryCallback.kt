package com.example.ruslanyussupov.popularmovies.data

import androidx.paging.PagedList
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.data.remote.ReviewsRequest
import timber.log.Timber


class ReviewBoundaryCallback(private val reviewsRequest: ReviewsRequest)
    : PagedList.BoundaryCallback<Review>() {

    override fun onZeroItemsLoaded() {
        Timber.d("onZeroItemsLoaded")
        reviewsRequest.resetPage()
        reviewsRequest.request()
    }

    override fun onItemAtEndLoaded(itemAtEnd: Review) {
        Timber.d("onItemAtEndLoaded")
        reviewsRequest.request()
    }

}