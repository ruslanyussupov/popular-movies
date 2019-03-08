package com.example.ruslanyussupov.popularmovies.adapters


import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.databinding.ItemMovieBinding

class MovieAdapter(private val onMovieClick: (movie: Movie) -> Unit)
    : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MOVIE_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = DataBindingUtil.inflate<ItemMovieBinding>(LayoutInflater.from(parent.context),
                R.layout.item_movie, parent, false)

        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = getItem(position) ?: return
        holder as MovieViewHolder
        holder.binding.movie = movie
        holder.binding.executePendingBindings()
        holder.itemView.setOnClickListener { onMovieClick(movie) }
    }

    class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return  oldItem == newItem
            }

        }
    }

    interface OnMovieClickListener {
        fun onMovieClick(movie: Movie)
    }

}


