package com.example.playlistmaker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()
    init {

        viewModelScope.launch {

            playlistsInteractor
                .getPlaylists()
                .collect { playlists ->

                    _playlists.value = playlists
                }
        }
    }

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

    private val _addTrackState =
        MutableLiveData<AddTrackState>()

    val addTrackState: LiveData<AddTrackState> =
        _addTrackState

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
            val isFav = favoriteTracksInteractor.isFavorite(track.trackId)

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

            val isFav = favoriteTracksInteractor.isFavorite(track.trackId)

            track.isFavorite = isFav
            _isFavorite.postValue(isFav)
        }
    }

    fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {

        if (playlist.trackIds.contains(track.trackId)) {

            _addTrackState.value =
                AddTrackState.AlreadyExists(
                    playlist.name
                )

            return
        }

        viewModelScope.launch {

            playlistsInteractor.addTrackToPlaylist(
                track,
                playlist
            )

            _addTrackState.postValue(
                AddTrackState.Success(
                    playlist.name
                )
            )
        }
    }
}
