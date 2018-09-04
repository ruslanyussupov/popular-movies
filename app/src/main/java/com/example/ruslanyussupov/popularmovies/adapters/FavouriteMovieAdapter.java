package com.example.ruslanyussupov.popularmovies.adapters;


import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ruslanyussupov.popularmovies.OnMovieClickListener;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.data.model.Movie;
import com.example.ruslanyussupov.popularmovies.databinding.ItemMovieBinding;

import java.util.List;

public class FavouriteMovieAdapter extends RecyclerView.Adapter<FavouriteMovieAdapter.ViewHolder> {

    private static final String LOG_TAG = FavouriteMovieAdapter.class.getSimpleName();

    private List<Movie> mFavMovies;
    private final OnMovieClickListener mMovieClickListener;

    public FavouriteMovieAdapter(List<Movie> movies, OnMovieClickListener movieClickListener) {
        mFavMovies = movies;
        mMovieClickListener = movieClickListener;
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
        holder.bind(mFavMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mFavMovies == null ? 0 : mFavMovies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemMovieBinding mBinding;

        ViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

            mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mMovieClickListener.onMovieClick(mFavMovies.get(position));
                }
            });

        }

        void bind(Movie movie) {
            mBinding.executePendingBindings();
            String posterPath = movie.getPosterLocalPath();
            Log.d(LOG_TAG, "Poster path: " + posterPath);
            Bitmap poster = BitmapFactory.decodeFile(posterPath);

            if (poster == null) {
                mBinding.moviePoster.setImageResource(R.drawable.poster_placeholder);
            } else {
                mBinding.moviePoster.setImageBitmap(poster);
            }
        }
    }

    public void updateData(List<Movie> movies) {
        mFavMovies = movies;
        notifyDataSetChanged();
        Log.d(LOG_TAG, "notifyDataSetChanged()");
    }

}
