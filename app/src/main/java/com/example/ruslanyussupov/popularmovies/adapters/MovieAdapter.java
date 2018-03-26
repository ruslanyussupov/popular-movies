package com.example.ruslanyussupov.popularmovies.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ruslanyussupov.popularmovies.OnMovieClickListener;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.model.Movie;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {


    private List<Movie> mMovies;
    private final OnMovieClickListener mMovieClickListener;

    public MovieAdapter(List<Movie> movies, OnMovieClickListener onMovieClickListener) {
        mMovies = movies;
        mMovieClickListener = onMovieClickListener;
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
        String posterPath = NetworkUtils.buildMoviePosterUrlPath(currentMovie.getPosterPath());
        Picasso.with(context).load(posterPath).placeholder(R.drawable.poster_placeholder)
                .error(R.drawable.poster_error)
                .into(holder.posterImageView);

    }

    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        }
        return mMovies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        // Define what to bind
        @BindView(R.id.movie_poster) ImageView posterImageView;

        ViewHolder(View itemView) {
            super(itemView);

            // Execute binding
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mMovieClickListener.onMovieClick(mMovies.get(position));
                }
            });

        }
    }

    public void updateData(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

}
