package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        repository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylist(playlistId: Long): Playlist? {
        return repository.getPlaylist(playlistId)
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override fun getTracks(
        trackIds: List<Int>
    ): Flow<List<Track>> {

        return repository.getTracks(trackIds)
    }

    override suspend fun removeTrackFromPlaylist(
        track: Track,
        playlist: Playlist
    ) {

        repository.removeTrackFromPlaylist(
            track,
            playlist
        )
    }
}