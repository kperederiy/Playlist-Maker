package com.example.playlistmaker.presentation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlaylistsInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _playlist = MutableStateFlow<Playlist?>(null)
    val playlist: StateFlow<Playlist?> = _playlist

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    private val _duration = MutableStateFlow("")
    val duration: StateFlow<String> = _duration

    fun loadPlaylist(playlistId: Long) {

        viewModelScope.launch {

            val playlist =
                playlistsInteractor.getPlaylist(playlistId)

            _playlist.value = playlist

            playlist?.let {

                observeTracks(it.trackIds)
            }
        }
    }

    private fun observeTracks(trackIds: List<Int>) {

        viewModelScope.launch {

            playlistsInteractor.getTracks(trackIds)
                .collect { tracks ->

                    _tracks.value = tracks

                    calculateDuration(tracks)
                }
        }
    }

//    private fun calculateDuration(tracks: List<Track>) {
//
//        val durationSum = tracks.sumOf { track ->
//
//            track.trackTime
//                .split(":")
//                .let { time ->
//
//                    val minutes = time[0].toInt()
//                    val seconds = time[1].toInt()
//
//                    (minutes * 60 + seconds) * 1000L
//                }
//        }
//
//        val minutes = SimpleDateFormat(
//            "mm",
//            Locale.getDefault()
//        ).format(durationSum)
//
//        _duration.value = "$minutes мин"
//    }
    private fun calculateDuration(tracks: List<Track>) {

        val totalSeconds = tracks.sumOf { track ->

            val parts = track.trackTime.split(":")

            val minutes = parts[0].toInt()
            val seconds = parts[1].toInt()

            minutes * 60 + seconds
        }

        val totalMinutes = totalSeconds / 60

        _duration.value = totalMinutes.toString()
    }
}