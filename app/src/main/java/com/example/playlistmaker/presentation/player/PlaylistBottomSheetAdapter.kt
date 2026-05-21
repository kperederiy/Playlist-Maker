package com.example.playlistmaker.presentation.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.example.playlistmaker.domain.model.Playlist

class PlaylistBottomSheetAdapter :
    ListAdapter<Playlist, PlaylistBottomSheetViewHolder>(DiffCallback()) {

    var onPlaylistClick: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistBottomSheetViewHolder {

        val binding = ItemPlaylistBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PlaylistBottomSheetViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PlaylistBottomSheetViewHolder,
        position: Int
    ) {

        val playlist = getItem(position)

        holder.bind(playlist)

        holder.itemView.setOnClickListener {
            onPlaylistClick?.invoke(playlist)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Playlist>() {

        override fun areItemsTheSame(
            oldItem: Playlist,
            newItem: Playlist
        ): Boolean {

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Playlist,
            newItem: Playlist
        ): Boolean {

            return oldItem == newItem
        }
    }
}