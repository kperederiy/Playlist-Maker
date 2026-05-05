package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun addTrack(track: Track)

    suspend fun removeTrack(track: Track)

    fun getAllTracks(): Flow<List<Track>>

    suspend fun getFavoriteTrackIds(): List<Int>

    suspend fun isTrackFavorite(trackId: Int): Boolean
}