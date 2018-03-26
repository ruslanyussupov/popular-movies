package com.example.ruslanyussupov.popularmovies.events;


import com.example.ruslanyussupov.popularmovies.model.Movie;

public class AddFavouriteEvent {

    private final Movie movie;

    public AddFavouriteEvent(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

}
