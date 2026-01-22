package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface TracksRepository {
    fun searchTracks(query: String, callback: (List<Track>) -> Unit)
}
