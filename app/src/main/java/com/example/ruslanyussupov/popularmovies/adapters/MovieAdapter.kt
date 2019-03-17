package com.example.ruslanyussupov.popularmovies.adapters


import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.databinding.ItemLoadingBinding
import com.example.ruslanyussupov.popularmovies.databinding.ItemMovieBinding
import timber.log.Timber

class MovieAdapter(private val onMovieClick: (movie: Movie) -> Unit)
    : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MOVIE_COMPARATOR) {

    val isLoading = ObservableBoolean(false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            TYPE_MOVIE -> {
                val binding = DataBindingUtil.inflate<ItemMovieBinding>(LayoutInflater.from(parent.context),
                        R.layout.item_movie, parent, false)
                MovieViewHolder(binding)
            }
            TYPE_LOADING -> {
                val binding = DataBindingUtil.inflate<ItemLoadingBinding>(LayoutInflater.from(parent.context),
                        R.layout.item_loading, parent, false)
                LoadingViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type $viewType")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            TYPE_MOVIE -> {
                val movie = getItem(position) ?: return
                holder as MovieViewHolder
                holder.binding.movie = movie
                holder.binding.executePendingBindings()
                holder.itemView.setOnClickListener { onMovieClick(movie) }
            }
            TYPE_LOADING -> {
                holder as LoadingViewHolder
                holder.binding.isLoading = isLoading
                val layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                layoutParams.isFullSpan = true
            }
            else -> throw IllegalArgumentException("Invalid view type $viewType")
        }

    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == super.getItemCount()) {
            TYPE_LOADING
        } else {
            TYPE_MOVIE
        }
    }

    override fun submitList(pagedList: PagedList<Movie>?) {
        Timber.d("List submitted ${pagedList?.size}")
        removeLoadingItem()
        super.submitList(pagedList)
    }

    private fun removeLoadingItem() {
        val position = itemCount - 1
        Timber.d("Removing loading item at position $position")
        notifyItemRemoved(position)
    }

    class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    class LoadingViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {

        private const val TYPE_MOVIE = 0
        private const val TYPE_LOADING = 1

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


