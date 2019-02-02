package com.example.ruslanyussupov.popularmovies.adapters


import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.databinding.ItemReviewBinding

class ReviewAdapter(private var reviews: List<Review>,
                    private val onReviewClick: (review: Review) -> Unit)
    : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    private lateinit var binding: ItemReviewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_review, parent, false)

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        binding.executePendingBindings()
        binding.review = review
        holder.itemView.setOnClickListener { onReviewClick(review) }
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    fun updateData(reviews: List<Review>) {
        this.reviews = reviews
        notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}
