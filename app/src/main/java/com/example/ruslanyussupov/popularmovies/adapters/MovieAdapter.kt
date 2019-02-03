package com.example.ruslanyussupov.popularmovies.adapters


import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil

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

    init {
        App.component?.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = DataBindingUtil.inflate<ItemMovieBinding>(LayoutInflater.from(parent.context),
                R.layout.item_movie, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.binding.movie = movie
        holder.binding.executePendingBindings()
        holder.itemView.setOnClickListener { onMovieClick(movie) }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun updateData(newMovieList: List<Movie>) {
        val diffResult = DiffUtil.calculateDiff(MovieDiffCallback(movies, newMovieList), true)
        movies = newMovieList
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    class MovieDiffCallback(private val oldMovieList: List<Movie>,
                            private val newMovieList: List<Movie>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldMovieList[oldItemPosition].id == newMovieList[newItemPosition].id
        }

        override fun getOldListSize() = oldMovieList.size

        override fun getNewListSize() = newMovieList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return  newMovieList[newItemPosition].posterPath == oldMovieList[oldItemPosition].posterPath &&
                    newMovieList[newItemPosition].originalTitle == oldMovieList[oldItemPosition].originalTitle
        }

    }

    interface OnMovieClickListener {
        fun onMovieClick(movie: Movie)
    }

}


