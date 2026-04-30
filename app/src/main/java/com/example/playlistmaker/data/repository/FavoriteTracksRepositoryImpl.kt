package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.FavoriteTrackDao
import com.example.playlistmaker.data.db.FavoriteTrackEntity
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val dao: FavoriteTrackDao
) : FavoriteTracksRepository {

    override suspend fun addTrack(track: Track) {
        dao.insertTrack(track.toEntity())
    }

    override suspend fun removeTrack(track: Track) {
        dao.deleteTrack(track.toEntity())
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return dao.getAllTracks()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return dao.getAllTrackIds()
    }

    // 🔽 Маппинг Track → Entity
    private fun Track.toEntity(): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            trackId = trackId,
            artworkUrl100 = artworkUrl100,
            trackName = trackName,
            artistName = artistName,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            trackTimeMillis = trackTime,
            previewUrl = previewUrl,
            addedAt = System.currentTimeMillis()
        )
    }

    // 🔽 Маппинг Entity → Track
    private fun FavoriteTrackEntity.toDomain(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTime = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl,
            isFavorite = true // Всё, что приходит из БД — уже избранное
        )
    }
}