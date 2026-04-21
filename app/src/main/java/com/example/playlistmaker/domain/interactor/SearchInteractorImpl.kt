package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.Resource
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SearchInteractorImpl(
    private val tracksRepository: TracksRepository
) : SearchInteractor {

    override fun searchTracks(query: String): Flow<Resource<List<Track>>> {

        if (query.isBlank()) {
            return flowOf(Resource.Success(emptyList()))
        }

        return tracksRepository.searchTracks(query)
    }
}
