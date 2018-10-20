package com.example.ruslanyussupov.popularmovies.adapters;


import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ruslanyussupov.popularmovies.App;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.Utils;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.databinding.ItemMovieBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {


    private List<Movie> mMovies;
    private final OnMovieClickListener mMovieClickListener;

    @Inject
    Utils utils;

    public MovieAdapter(List<Movie> movies, OnMovieClickListener onMovieClickListener) {
        mMovies = movies;
        mMovieClickListener = onMovieClickListener;
        App.getComponent().inject(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemMovieBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_movie, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemMovieBinding mBinding;

        ViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());

            mBinding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                mMovieClickListener.onMovieClick(mMovies.get(position));
            });

        }

        void bind(Movie movie) {
            mBinding.executePendingBindings();
            if (utils.hasNetworkConnection()) {
                Picasso.get().load(movie.getFullPosterPath()).placeholder(R.drawable.poster_placeholder)
                        .error(R.drawable.poster_error)
                        .into(mBinding.moviePoster);
            } else {
                Bitmap poster = BitmapFactory.decodeFile(movie.getPosterLocalPath());

                if (poster == null) {
                    mBinding.moviePoster.setImageResource(R.drawable.backdrop_placeholder);
                } else {
                    mBinding.moviePoster.setImageBitmap(poster);
                }
            }

        }
    }

    public void updateData(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

}
