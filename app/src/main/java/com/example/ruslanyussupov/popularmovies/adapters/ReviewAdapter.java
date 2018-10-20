package com.example.ruslanyussupov.popularmovies.adapters;


import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.data.model.Review;
import com.example.ruslanyussupov.popularmovies.databinding.ItemReviewBinding;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> mReviews;
    private final OnReviewClickListener mOnReviewClickListener;

    public ReviewAdapter(List<Review> reviews, OnReviewClickListener onReviewClickListener) {
        mReviews = reviews;
        mOnReviewClickListener = onReviewClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemReviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_review, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mReviews.get(position));
    }

    @Override
    public int getItemCount() {
        return mReviews == null ? 0 : mReviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemReviewBinding mBinding;

        ViewHolder(ItemReviewBinding binding) {
            super(binding.getRoot());

            mBinding = binding;

            binding.getRoot().setOnClickListener(v -> mOnReviewClickListener
                    .onReviewClick(mReviews.get(getAdapterPosition())));
        }

        void bind(Review review) {
            mBinding.executePendingBindings();
            mBinding.setReview(review);
        }
    }

    public void updateData(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    public interface OnReviewClickListener {
        void onReviewClick(Review review);
    }

}
