package com.example.playlistmaker.domain.interactor

interface AudioPlayerInteractor {
    fun preparePlayer(url: String)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun setOnPreparedListener(listener: () -> Unit)
    fun setOnCompletionListener(listener: () -> Unit)
}
