package com.example.playlistmaker.presentation.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : NewPlaylistViewModel(playlistsInteractor) {

    private val _playlist =
        MutableLiveData<Playlist>()

    val playlist: LiveData<Playlist> =
        _playlist

    fun setPlaylist(playlist: Playlist) {

        _playlist.value = playlist
    }

    fun updatePlaylist(
        name: String,
        description: String,
        coverPath: String
    ) {

        val oldPlaylist =
            _playlist.value ?: return

        viewModelScope.launch {

            val updatedPlaylist =
                oldPlaylist.copy(
                    name = name,
                    description = description,
                    coverPath = coverPath
                )

            playlistsInteractor.updatePlaylist(
                updatedPlaylist
            )
        }
    }
}