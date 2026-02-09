package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track

interface SearchInteractor {
    fun searchTracks(
        query: String,
        onResult: (List<Track>) -> Unit,
        onError: () -> Unit,
        onNetworkError: () -> Unit
    )
}
