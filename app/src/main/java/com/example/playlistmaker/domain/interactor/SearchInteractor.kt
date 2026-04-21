package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.Resource
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTracks(query: String): Flow<Resource<List<Track>>>
}
