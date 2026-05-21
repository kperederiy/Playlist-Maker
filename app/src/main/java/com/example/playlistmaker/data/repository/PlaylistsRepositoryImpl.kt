package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistDbConverter
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistTrackDao
import com.example.playlistmaker.data.db.PlaylistTrackDbConverter
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val gson: Gson,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter
) : PlaylistsRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(playlist.toEntity())
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist.toEntity())
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist.toEntity())
    }

    override fun getPlaylists(): Flow<List<Playlist>> {

        return playlistDao.getPlaylists()
            .map { list ->
                list.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun getPlaylist(playlistId: Long): Playlist? {

        return playlistDao.getPlaylistById(playlistId)
            ?.toDomain()
    }

    private fun Playlist.toEntity(): PlaylistEntity {

        return PlaylistEntity(
            id = id,
            name = name,
            description = description,
            coverPath = coverPath,
            trackIds = gson.toJson(trackIds),
            tracksCount = tracksCount
        )
    }

    private fun PlaylistEntity.toDomain(): Playlist {

        return Playlist(
            id = id,
            name = name,
            description = description,
            coverPath = coverPath,
            trackIds = gson.fromJson(
                trackIds,
                Array<Int>::class.java
            ).toList(),
            tracksCount = tracksCount
        )
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {

        val updatedTrackIds = playlist.trackIds.toMutableList()
        updatedTrackIds.add(track.trackId)

        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = playlist.tracksCount + 1
        )

        playlistDao.updatePlaylist(
            playlistDbConverter.map(updatedPlaylist)
        )

        playlistTrackDao.insertTrack(
            playlistTrackDbConverter.map(track)
        )
    }

    override fun getTracks(
        trackIds: List<Int>
    ): Flow<List<Track>> {

        return playlistTrackDao.getTracksByIds(trackIds)
            .map { tracks ->

                tracks.map { entity ->

                    Track(
                        trackId = entity.trackId,
                        trackName = entity.trackName,
                        artistName = entity.artistName,
                        trackTime = entity.trackTime,
                        artworkUrl100 = entity.artworkUrl100,
                        collectionName = entity.collectionName,
                        releaseDate = entity.releaseDate,
                        primaryGenreName = entity.primaryGenreName,
                        country = entity.country,
                        previewUrl = entity.previewUrl
                    )
                }
            }
    }
}