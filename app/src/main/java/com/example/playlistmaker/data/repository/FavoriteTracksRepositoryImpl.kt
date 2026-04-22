package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.FavoriteTrackEntity
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val database: AppDatabase
) : FavoriteTracksRepository {

    override suspend fun addTrack(track: Track) {
        database.favoriteTrackDao().insertTrack(track.toEntity())
    }

    override suspend fun removeTrack(track: Track) {
        database.favoriteTrackDao().deleteTrack(track.toEntity())
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return database.favoriteTrackDao()
            .getAllTracks()
            .map { list -> list.map { it.toDomain() } }
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
            previewUrl = previewUrl
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