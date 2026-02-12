package com.example.playlistmaker.presentation.player

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactor.AudioPlayerInteractor

class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor
) : ViewModel() {

    private val _state = MutableLiveData(
        AudioPlayerState(
            isPlayButtonEnabled = false,
            isPlaying = false,
            currentTime = "00:00"
        )
    )
    val state: LiveData<AudioPlayerState> = _state


    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())

    fun prepare(url: String) {
        interactor.preparePlayer(url)

        interactor.setOnPreparedListener {
            _state.postValue(
                AudioPlayerState(
                    isPlayButtonEnabled = true,
                    isPlaying = false,
                    currentTime = "00:00"
                )
            )
        }

        interactor.setOnCompletionListener {
            isPlaying = false
            _state.postValue(
                AudioPlayerState(true, false, "00:00")
            )
        }
    }

    fun onPlayClicked() {
        if (isPlaying) {
            pause()
        } else {
            play()
        }
    }

    private fun play() {
        interactor.play()
        isPlaying = true
        updateTime()
        _state.postValue(_state.value?.copy(isPlaying = true))
    }

    private fun pause() {
        interactor.pause()
        isPlaying = false
        _state.postValue(_state.value?.copy(isPlaying = false))
    }

    private fun updateTime() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isPlaying) {
                    val time = formatTime(interactor.getCurrentPosition())
                    _state.postValue(_state.value?.copy(currentTime = time))
                    handler.postDelayed(this, 300)
                }
            }
        }, 300)
    }

    private fun formatTime(millis: Int): String {
        val minutes = millis / 1000 / 60
        val seconds = millis / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        interactor.release()
    }
}
