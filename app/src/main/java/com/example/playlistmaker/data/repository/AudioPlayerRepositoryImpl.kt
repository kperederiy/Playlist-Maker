package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.repository.AudioPlayerRepository

class AudioPlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : AudioPlayerRepository {

    override fun prepare(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun reset() {
        mediaPlayer.reset()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        mediaPlayer.setOnPreparedListener { listener() }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener { listener() }
    }
}
