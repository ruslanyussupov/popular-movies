package com.example.ruslanyussupov.popularmovies.adapters;


import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.data.model.Video;
import com.example.ruslanyussupov.popularmovies.databinding.ItemVideoBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private List<Video> mVideos;
    private final OnVideoClickListener mOnVideoClickListener;

    public VideoAdapter(List<Video> videos, OnVideoClickListener onVideoClickListener) {
        mVideos = videos;
        mOnVideoClickListener = onVideoClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVideoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_video, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mVideos.get(position));
    }

    @Override
    public int getItemCount() {
        return mVideos == null ? 0 : mVideos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemVideoBinding mBinding;

        ViewHolder(ItemVideoBinding binding) {
            super(binding.getRoot());

            mBinding = binding;

            binding.getRoot().setOnClickListener(v -> mOnVideoClickListener
                    .onVideoClick(mVideos.get(getAdapterPosition())));
        }

        void bind(Video video) {
            mBinding.executePendingBindings();
            Picasso.get().load(video.getPreviewImagePath())
                    .placeholder(R.drawable.video_preview_placeholder)
                    .error(R.drawable.video_preview_placeholder)
                    .into(mBinding.videoPreviewIv);
        }
    }

    public void updateData(List<Video> videos) {
        mVideos = videos;
        notifyDataSetChanged();
    }

    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }

}
