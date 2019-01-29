package com.example.ruslanyussupov.popularmovies.adapters


import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.data.model.Video
import com.example.ruslanyussupov.popularmovies.databinding.ItemVideoBinding
import com.squareup.picasso.Picasso

class VideoAdapter(private var videos: List<Video>,
                   private val onVideoClick: (video: Video) -> Unit)
    : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemVideoBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_video, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    inner class ViewHolder(private val mBinding: ItemVideoBinding) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(video: Video) {
            mBinding.executePendingBindings()

            itemView.setOnClickListener { onVideoClick(video) }

            Picasso.get()
                    .load(video.previewImagePath)
                    .placeholder(R.drawable.video_preview_placeholder)
                    .error(R.drawable.video_preview_placeholder)
                    .into(mBinding.videoPreviewIv)
        }
    }

    fun updateData(videos: List<Video>) {
        this.videos = videos
        notifyDataSetChanged()
    }

}
