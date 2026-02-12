package com.example.playlistmaker.presentation.player

data class AudioPlayerState(
    val isPlayButtonEnabled: Boolean,
    val isPlaying: Boolean,
    val currentTime: String
)
