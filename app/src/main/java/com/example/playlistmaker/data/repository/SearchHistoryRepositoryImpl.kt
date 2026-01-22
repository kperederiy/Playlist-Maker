package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val storage: SearchHistoryStorage
) : SearchHistoryRepository {

    override fun saveTrack(track: Track) {
        val history = getHistory().toMutableList()

        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > MAX_SIZE) {
            history.removeAt(history.lastIndex)
        }

        val dtoList = history.map { it.toDto() }
        storage.save(dtoList)
    }

    override fun getHistory(): List<Track> {
        return storage.get().map { it.toDomain() }
    }

    override fun clearHistory() {
        storage.clear()
    }

    companion object {
        private const val MAX_SIZE = 10
    }
}
