package com.example.playlistmaker.presentation.player

sealed interface AddTrackState {

    data class Success(
        val playlistName: String
    ) : AddTrackState

    data class AlreadyExists(
        val playlistName: String
    ) : AddTrackState
}