package com.example.ruslanyussupov.popularmovies.adapters


import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.ruslanyussupov.popularmovies.App
import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.Utils
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.databinding.ItemMovieBinding

import javax.inject.Inject


class MovieAdapter(private var movies: List<Movie>,
                   private val onMovieClick: (movie: Movie) -> Unit)
    : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    @Inject
    internal lateinit var utils: Utils

    private lateinit var binding: ItemMovieBinding

    init {
        App.component?.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_movie, parent, false)

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        binding.executePendingBindings()
        binding.movie = movie
        holder.itemView.setOnClickListener { onMovieClick(movie) }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun updateData(movies: List<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface OnMovieClickListener {
        fun onMovieClick(movie: Movie)
    }

}


