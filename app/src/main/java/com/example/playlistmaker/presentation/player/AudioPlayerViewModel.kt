package com.example.playlistmaker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val _state = MutableLiveData(
        AudioPlayerState(
            isPlayButtonEnabled = false,
            isPlaying = false,
            currentTime = "00:00"
        )
    )
    val state: LiveData<AudioPlayerState> = _state

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite


    private var isPlaying = false
    private var timerJob: Job? = null
    private var currentTrack: Track? = null
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
            timerJob?.cancel()

            _state.postValue(
                AudioPlayerState(
                    isPlayButtonEnabled = true,
                    isPlaying = false,
                    currentTime = "00:00"
                )
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

        _state.postValue(_state.value?.copy(isPlaying = true))

        startTimer()
    }

    private fun pause() {
        interactor.pause()
        isPlaying = false

        timerJob?.cancel()

        _state.postValue(_state.value?.copy(isPlaying = false))
    }

    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (isPlaying) {
                delay(300L)

                val time = formatTime(interactor.getCurrentPosition())

                _state.postValue(
                    _state.value?.copy(currentTime = time)
                )
            }
        }
    }

    private fun formatTime(millis: Int): String {
        val minutes = millis / 1000 / 60
        val seconds = millis / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        interactor.reset()
    }

    fun setTrack(track: Track) {
        currentTrack = track

        viewModelScope.launch {
            val favoriteIds = favoriteTracksInteractor.getFavoriteTrackIds()

            val isFav = favoriteIds.contains(track.trackId)

            track.isFavorite = isFav
            _isFavorite.postValue(isFav)
        }
    }

    fun onFavoriteClicked() {
        val track = currentTrack ?: return

        viewModelScope.launch {
            if (track.isFavorite) {
                favoriteTracksInteractor.removeTrack(track)
            } else {
                favoriteTracksInteractor.addTrack(track)
            }

            track.isFavorite = !track.isFavorite
            _isFavorite.postValue(track.isFavorite)
        }
    }
}
