package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.Resource
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(query: String): Flow<Resource<List<Track>>>
}

