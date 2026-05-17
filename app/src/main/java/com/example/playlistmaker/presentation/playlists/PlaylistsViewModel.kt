package com.example.playlistmaker.presentation.playlists

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactor.PlaylistsInteractor

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    fun getPlaylists() = playlistsInteractor.getPlaylists()
}