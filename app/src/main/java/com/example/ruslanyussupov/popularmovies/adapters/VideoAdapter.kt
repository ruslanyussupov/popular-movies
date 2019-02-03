package com.example.ruslanyussupov.popularmovies.adapters


import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.data.model.Video
import com.example.ruslanyussupov.popularmovies.databinding.ItemVideoBinding

class VideoAdapter(private var videos: List<Video>,
                   private val onVideoClick: (video: Video) -> Unit)
    : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemVideoBinding>(LayoutInflater.from(parent.context),
                R.layout.item_video, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videos[position]
        holder.binding.executePendingBindings()
        holder.binding.video = video
        holder.itemView.setOnClickListener { onVideoClick(video) }
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    fun updateData(videos: List<Video>) {
        this.videos = videos
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

}
