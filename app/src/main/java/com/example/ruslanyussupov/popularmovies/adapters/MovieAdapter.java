package com.example.ruslanyussupov.popularmovies.adapters;


import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ruslanyussupov.popularmovies.OnMovieClickListener;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.databinding.ItemMovieBinding;
import com.example.ruslanyussupov.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {


    private List<Movie> mMovies;
    private final OnMovieClickListener mMovieClickListener;

    public MovieAdapter(List<Movie> movies, OnMovieClickListener onMovieClickListener) {
        mMovies = movies;
        mMovieClickListener = onMovieClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate the our custom layout
        ItemMovieBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_movie, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        }
        return mMovies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemMovieBinding mBinding;

        ViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());

            mBinding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mMovieClickListener.onMovieClick(mMovies.get(position));
                }
            });

        }

        void bind(int position) {
            // Get appropriate movie
            Movie currentMovie = mMovies.get(position);

            // Load movie poster into ImageView
            String posterPath = NetworkUtils.buildMoviePosterUrlPath(currentMovie.getPosterPath());
            Picasso.get().load(posterPath).placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_error)
                    .into(mBinding.moviePoster);
        }
    }

    public void updateData(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

}
