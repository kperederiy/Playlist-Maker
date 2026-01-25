package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val storage: SearchHistoryStorage
) : SearchHistoryRepository {

    override fun saveTrack(track: Track) {
        val historyDto = storage.get().toMutableList()

        historyDto.removeAll { it.trackId == track.trackId }

        historyDto.add(0, track.toDto())

        if (historyDto.size > MAX_SIZE) {
            historyDto.removeAt(historyDto.lastIndex)
        }

        storage.save(historyDto)
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
