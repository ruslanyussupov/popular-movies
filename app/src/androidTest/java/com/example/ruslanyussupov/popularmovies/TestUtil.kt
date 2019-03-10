package com.example.ruslanyussupov.popularmovies

import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.data.model.Video

fun createMovies(): List<Movie> {

    return listOf(
            Movie(399579,
                    "Alita: Battle Angel",
                    "/xRWht48C2V8XNfzvPehyClOvDni.jpg",
                    "When Alita awakens with no memory of who she is in a future world...",
                    8.5,
                    "2019-01-31",
                    "/aQXTw3wIWuFMy0beXRiZ1xVKtcf.jpg",
                    "/cache/posters/poster-399579.jpg",
                    "cache/backdrops/backdrop-399579.jpg"),
            Movie(0,
                    null,
                    null,
                    null,
                    0.0,
                    null,
                    null),
            Movie(450465,
                    "Glass",
                    "/svIDTNUoajS8dLEo7EosxvyAsgJ.jpg",
                    "In a series of escalating encounters, security guard David Dunn uses his...",
                    6.6,
                    "2019-01-16",
                    "/lvjscO8wmpEbIfOEZi92Je8Ktlg.jpg")
    )

}

fun createReviews(): List<Review> {
    return listOf(
            Review("r100",
                    "John Doe",
                    "Awesome!",
                    "https://www.themoviedb.org"),
            Review("r200",
                    "Jane Doe",
                    "Cool!",
                    "https://www.themoviedb.org"),
            Review("r300",
                    "Rick Sanchez",
                    "wubba lubba dub dub",
                    "https://www.themoviedb.org"),
            Review("r400",
                    "Jane Doe",
                    "Super!",
                    "https://www.themoviedb.org"),
            Review("r500",
                    null,
                    null,
                    null)
    )
}

fun createVideos(): List<Video> {
    return listOf(
            Video("v100",
                    "lskdjf213",
                    "Trailer 1",
                    "https://www.youtube.com",
                    300),
            Video("v200",
                    "dfgruy132",
                    "Trailer 2",
                    "https://www.youtube.com",
                    null),
            Video("v300",
                    null,
                    null,
                    null,
                    null)
    )
}