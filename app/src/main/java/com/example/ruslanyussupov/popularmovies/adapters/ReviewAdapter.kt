package com.example.ruslanyussupov.popularmovies.adapters


import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.*

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.databinding.ItemReviewBinding
import timber.log.Timber
import kotlin.IllegalArgumentException

class ReviewAdapter(private val header: View,
                    private val onReviewClick: (review: Review) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val adapterListUpdateCallback = AdapterListUpdateCallback(this)
    private val differ = AsyncPagedListDiffer<Review>(object : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            Timber.d("onChanged position: $position count: $count")
            adapterListUpdateCallback.onChanged(position + 1, count, payload)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            Timber.d("onMoved from $fromPosition to $toPosition")
            adapterListUpdateCallback.onMoved(fromPosition + 1, toPosition + 1)
        }

        override fun onInserted(position: Int, count: Int) {
            Timber.d("onInserted position: $position count: $count")
            adapterListUpdateCallback.onInserted(position + 1, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            Timber.d("onRemoved position: $position count: $count")
            adapterListUpdateCallback.onRemoved(position + 1, count)
        }

    }, AsyncDifferConfig.Builder<Review>(REVIEW_COMPARATOR).build())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(header)
            TYPE_REVIEW -> {
                ReviewViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.item_review, parent, false))
            }
            else -> throw IllegalArgumentException("Invalid view type.")
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_REVIEW
        //return if (differ.itemCount == 0) TYPE_NO_REVIEW else TYPE_REVIEW
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_REVIEW) {
            Timber.d("onBindViewHolder position: $position")
            val review = differ.getItem(position - 1) ?: return
            holder as ReviewViewHolder
            bindReviewHolder(holder, review, onReviewClick)
        }
    }

    override fun getItemCount(): Int {
        val count = differ.itemCount
        Timber.d("itemCount: $count")
        return count + 1 // plus header
    }

    private fun bindReviewHolder(holder: ReviewViewHolder, review: Review, onReviewClick: (review: Review) -> Unit) {
        holder.binding.review = review
        holder.binding.executePendingBindings()
        holder.itemView.setOnClickListener { onReviewClick(review) }
    }

    fun submitList(pagedList: PagedList<Review>) {
        differ.submitList(pagedList)
    }

    class ReviewViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_REVIEW = 1

        val REVIEW_COMPARATOR = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }

        }

    }

}


