package com.example.ruslanyussupov.popularmovies.adapters


import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.databinding.ItemReviewBinding
import kotlin.IllegalArgumentException

class ReviewAdapter(private var reviews: List<Review>,
                    private val header: View,
                    private val onReviewClick: (review: Review) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(header)
            TYPE_NO_REVIEW -> NoReviewViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_review_empty, parent, false))
            TYPE_REVIEW -> {
                ReviewViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_review, parent, false))
            }
            else -> throw IllegalArgumentException()
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return TYPE_HEADER
        return if (reviews.isEmpty()) TYPE_NO_REVIEW else TYPE_REVIEW
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_REVIEW) {
            val review = reviews[position-1]
            holder as ReviewViewHolder
            bindReviewHolder(holder, review, onReviewClick)
        }
    }

    override fun getItemCount(): Int {
        return if (reviews.isEmpty()) reviews.size + 2 else reviews.size + 1 // plus header and empty view
    }

    fun updateData(reviews: List<Review>) {
        this.reviews = reviews
        notifyDataSetChanged()
    }

    private fun bindReviewHolder(holder: ReviewViewHolder, review: Review, onReviewClick: (review: Review) -> Unit) {
        holder.binding.review = review
        holder.binding.executePendingBindings()
        holder.itemView.setOnClickListener { onReviewClick(review) }
    }

    class ReviewViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    class NoReviewViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_NO_REVIEW = 1
        private const val TYPE_REVIEW = 2
    }

}


