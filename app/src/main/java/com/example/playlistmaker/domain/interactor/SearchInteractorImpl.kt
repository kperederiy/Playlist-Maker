package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository

class SearchInteractorImpl(
    private val tracksRepository: TracksRepository
) : SearchInteractor {

    override fun searchTracks(
        query: String,
        onResult: (List<Track>) -> Unit,
        onError: () -> Unit
    ) {
        if (query.isBlank()) {
            onResult(emptyList())
            return
        }

        tracksRepository.searchTracks(
            query = query,
            onSuccess = { tracks ->
                onResult(tracks)
            },
            onError = {
                onError()
            }
        )
    }
}
