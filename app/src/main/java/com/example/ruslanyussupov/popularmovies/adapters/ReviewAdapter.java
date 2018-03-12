package com.example.ruslanyussupov.popularmovies.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.model.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> mReviews;
    private OnReviewClickListener mOnReviewClickListener;

    public ReviewAdapter(List<Review> reviews, OnReviewClickListener onReviewClickListener) {
        mReviews = reviews;
        mOnReviewClickListener = onReviewClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View reviewView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);

        return new ViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Review currentReview = mReviews.get(position);

        holder.mAuthorTv.setText(currentReview.getAuthor());
        holder.mContentTv.setText(currentReview.getContent());

    }

    @Override
    public int getItemCount() {
        if (mReviews == null) {
            return 0;
        }
        return mReviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.author_tv)TextView mAuthorTv;
        @BindView(R.id.content_tv)TextView mContentTv;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnReviewClickListener.onReviewClick(getAdapterPosition());
                }
            });
        }
    }

    public void updateData(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    public interface OnReviewClickListener {
        void onReviewClick(int position);
    }

}
