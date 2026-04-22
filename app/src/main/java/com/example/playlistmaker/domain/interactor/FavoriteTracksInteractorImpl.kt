package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        repository.removeTrack(track)
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return repository
            .getAllTracks()
            .map { tracks ->
                tracks.reversed() // новые сверху
            }
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return repository.getFavoriteTrackIds()
    }
}