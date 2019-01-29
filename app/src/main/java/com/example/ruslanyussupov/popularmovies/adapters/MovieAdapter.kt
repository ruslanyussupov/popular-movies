package com.example.ruslanyussupov.popularmovies.adapters


import androidx.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.Utils
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.databinding.ItemMovieBinding
import com.squareup.picasso.Picasso

import javax.inject.Inject


class MovieAdapter(private var movies: List<Movie>,
                   private val onMovieClick: (movie: Movie) -> Unit)
    : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    @Inject
    internal lateinit var utils: Utils

    init {
        App.component?.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding: ItemMovieBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_movie, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class ViewHolder(private val binding: ItemMovieBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.executePendingBindings()

            itemView.setOnClickListener { onMovieClick(movie) }

            if (utils.hasNetworkConnection()) {
                Picasso.get()
                        .load(movie.fullPosterPath)
                        .placeholder(R.drawable.poster_placeholder)
                        .error(R.drawable.poster_error)
                        .into(binding.moviePoster)
            } else {
                val poster = BitmapFactory.decodeFile(movie.posterLocalPath)

                if (poster == null) {
                    binding.moviePoster.setImageResource(R.drawable.backdrop_placeholder)
                } else {
                    binding.moviePoster.setImageBitmap(poster)
                }
            }

        }
    }

    fun updateData(movies: List<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    interface OnMovieClickListener {
        fun onMovieClick(movie: Movie)
    }

}


