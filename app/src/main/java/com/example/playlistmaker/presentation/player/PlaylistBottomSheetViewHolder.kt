package com.example.playlistmaker.presentation.player

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.example.playlistmaker.domain.model.Playlist
import java.io.File

class PlaylistBottomSheetViewHolder(
    private val binding: ItemPlaylistBottomSheetBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {

        binding.name.text = playlist.name

        binding.count.text =
            "${playlist.tracksCount} треков"

        if (playlist.coverPath.isNotEmpty()) {

            Glide.with(binding.root)
                .load(File(playlist.coverPath))
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.cover)

        } else {

            binding.cover.setImageResource(R.drawable.placeholder)
        }
    }
}