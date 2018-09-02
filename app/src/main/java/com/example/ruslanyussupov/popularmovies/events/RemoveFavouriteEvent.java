package com.example.ruslanyussupov.popularmovies.events;


import com.example.ruslanyussupov.popularmovies.data.model.Movie;

public class RemoveFavouriteEvent {

    private final Movie movie;

    public RemoveFavouriteEvent(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

}
