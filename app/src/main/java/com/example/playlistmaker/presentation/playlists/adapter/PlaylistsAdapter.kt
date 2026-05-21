package com.example.playlistmaker.presentation.playlists.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.presentation.playlists.adapter.PlaylistViewHolder

class PlaylistsAdapter :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun submitList(newList: List<Playlist>) {

        playlists.clear()
        playlists.addAll(newList)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolder {

        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int
    ) {

        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}