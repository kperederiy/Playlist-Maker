package com.example.playlistmaker.presentation.playlists.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.domain.model.Playlist

class PlaylistViewHolder(
    private val binding: ItemPlaylistBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {

        binding.name.text = playlist.name

        binding.tracksCount.text =
            "${playlist.tracksCount} треков"

        if (playlist.coverPath.isNotEmpty()) {

            Glide.with(itemView)
                .load(playlist.coverPath)
                .placeholder(R.drawable.placeholder)
                .into(binding.cover)

        } else {

            binding.cover.setImageResource(R.drawable.placeholder)
        }
    }
}