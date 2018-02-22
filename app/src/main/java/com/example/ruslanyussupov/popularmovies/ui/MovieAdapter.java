package com.example.ruslanyussupov.popularmovies.ui;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> mMovies;

    public MovieAdapter(List<Movie> movies) {
        mMovies = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the our custom layout
        View movieView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);

        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Context context = holder.itemView.getContext();

        // Get appropriate movie
        Movie currentMovie = mMovies.get(position);

        // Load movie poster into ImageView
        String posterPath = "http://image.tmdb.org/t/p/w185" + currentMovie.getPosterPath();
        Log.v(LOG_TAG, posterPath);
        Picasso.with(context).load(posterPath).into(holder.posterImageView);

    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        // Define what to bind
        @BindView(R.id.movie_poster) ImageView posterImageView;

        ViewHolder(View itemView) {
            super(itemView);

            // Execute binding
            ButterKnife.bind(this, itemView);

        }
    }

    public void updateData(List<Movie> movies) {
        Log.d(LOG_TAG, movies.toString());
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

}
