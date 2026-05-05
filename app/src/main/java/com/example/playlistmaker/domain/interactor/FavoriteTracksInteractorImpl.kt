package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow

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
        return repository.getAllTracks()
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return repository.getFavoriteTrackIds()
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return repository.isTrackFavorite(trackId)
    }
}