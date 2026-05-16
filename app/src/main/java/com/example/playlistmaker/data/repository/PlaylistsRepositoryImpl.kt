package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val dao: PlaylistDao,
    private val gson: Gson
) : PlaylistsRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        dao.insertPlaylist(playlist.toEntity())
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        dao.updatePlaylist(playlist.toEntity())
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        dao.deletePlaylist(playlist.toEntity())
    }

    override fun getPlaylists(): Flow<List<Playlist>> {

        return dao.getPlaylists()
            .map { list ->
                list.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun getPlaylist(playlistId: Long): Playlist? {

        return dao.getPlaylistById(playlistId)
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
}