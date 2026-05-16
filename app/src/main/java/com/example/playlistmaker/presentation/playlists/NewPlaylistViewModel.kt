package com.example.playlistmaker.presentation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    fun createPlaylist(
        name: String,
        description: String,
        coverPath: String
    ) {

        viewModelScope.launch {

            val playlist = Playlist(
                name = name,
                description = description,
                coverPath = coverPath,
                trackIds = emptyList(),
                tracksCount = 0
            )

            playlistsInteractor.createPlaylist(playlist)
        }
    }
}