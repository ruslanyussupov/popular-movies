package com.example.ruslanyussupov.popularmovies.adapters;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ruslanyussupov.popularmovies.OnMovieClickListener;
import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.model.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteMovieAdapter extends RecyclerView.Adapter<FavouriteMovieAdapter.ViewHolder> {

    private static final String LOG_TAG = FavouriteMovieAdapter.class.getSimpleName();

    private List<Movie> mFavMovies;
    private final OnMovieClickListener mMovieClickListener;

    public FavouriteMovieAdapter(List<Movie> movies, OnMovieClickListener movieClickListener) {
        mFavMovies = movies;
        mMovieClickListener = movieClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View movieView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);

        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Movie currentMovie = mFavMovies.get(position);
        String posterPath = currentMovie.getPosterLocalPath();
        Log.d(LOG_TAG, "Poster path: " + posterPath);
        Bitmap poster = BitmapFactory.decodeFile(posterPath);

        if (poster == null) {
            holder.mMoviePoster.setImageResource(R.drawable.poster_placeholder);
        } else {
            holder.mMoviePoster.setImageBitmap(poster);
        }

    }

    @Override
    public int getItemCount() {
        if (mFavMovies == null) {
            return 0;
        }
        return mFavMovies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster)ImageView mMoviePoster;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMovieClickListener.onMovieClick(mFavMovies.get(getAdapterPosition()));
                }
            });

        }
    }

    public void updateData(List<Movie> movies) {
        mFavMovies = movies;
        notifyDataSetChanged();
        Log.d(LOG_TAG, "notifyDataSetChanged()");
    }

}
