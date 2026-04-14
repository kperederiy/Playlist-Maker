package com.example.playlistmaker.domain.repository

interface AudioPlayerRepository {
    fun prepare(url: String)
    fun play()
    fun pause()
    fun release()
    fun reset()
    fun getCurrentPosition(): Int
    fun setOnPreparedListener(listener: () -> Unit)
    fun setOnCompletionListener(listener: () -> Unit)
}