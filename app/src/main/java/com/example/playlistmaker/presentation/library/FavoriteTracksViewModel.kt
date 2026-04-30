package com.example.playlistmaker.presentation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.launch

sealed class FavoriteTracksState {
    object Empty : FavoriteTracksState()
    data class Content(val tracks: List<Track>) : FavoriteTracksState()
}

class FavoriteTracksViewModel(
    private val interactor: FavoriteTracksInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoriteTracksState>()
    val state: LiveData<FavoriteTracksState> = _state

    init {
        viewModelScope.launch {
            interactor.getAllTracks().collect { tracks ->
                if (tracks.isEmpty()) {
                    _state.value = FavoriteTracksState.Empty
                } else {
                    _state.value = FavoriteTracksState.Content(tracks)
                }
            }
        }
    }
}