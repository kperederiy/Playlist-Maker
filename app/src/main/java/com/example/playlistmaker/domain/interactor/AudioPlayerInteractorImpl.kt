package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.AudioPlayerRepository

class AudioPlayerInteractorImpl(
    private val repository: AudioPlayerRepository
) : AudioPlayerInteractor {

    override fun preparePlayer(url: String) {
        repository.prepare(url)
    }

    override fun play() {
        repository.play()
    }

    override fun pause() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }
    override fun reset() {
        repository.reset()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        repository.setOnPreparedListener(listener)
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        repository.setOnCompletionListener(listener)
    }
}
