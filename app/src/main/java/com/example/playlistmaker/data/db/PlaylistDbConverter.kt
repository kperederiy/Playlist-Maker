package com.example.playlistmaker.data.db

import com.example.playlistmaker.domain.model.Playlist
import com.google.gson.Gson

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = Gson().toJson(playlist.trackIds),
            tracksCount = playlist.tracksCount
        )
    }
}