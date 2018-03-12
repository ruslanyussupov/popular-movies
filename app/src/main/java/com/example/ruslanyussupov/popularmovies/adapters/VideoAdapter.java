package com.example.ruslanyussupov.popularmovies.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ruslanyussupov.popularmovies.R;
import com.example.ruslanyussupov.popularmovies.model.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private List<Video> mVideos;
    private OnVideoClickListener mOnVideoClickListener;

    public VideoAdapter(List<Video> videos, OnVideoClickListener onVideoClickListener) {
        mVideos = videos;
        mOnVideoClickListener = onVideoClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View videoView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);

        return new ViewHolder(videoView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Context context = holder.itemView.getContext();

        Video currentVideo = mVideos.get(position);

        Picasso.with(context).load(currentVideo.getPreviewImagePath())
                .placeholder(R.drawable.video_preview_placeholder)
                .error(R.drawable.video_preview_placeholder)
                .into(holder.mVideoPreviewIv);

    }

    @Override
    public int getItemCount() {

        if (mVideos == null) {
            return 0;
        }

        return mVideos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.video_preview_iv)ImageView mVideoPreviewIv;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnVideoClickListener.onVideoClick(getAdapterPosition());
                }
            });
        }
    }

    public void updateData(List<Video> videos) {
        mVideos = videos;
        notifyDataSetChanged();
    }

    public interface OnVideoClickListener {
        void onVideoClick(int position);
    }

}
