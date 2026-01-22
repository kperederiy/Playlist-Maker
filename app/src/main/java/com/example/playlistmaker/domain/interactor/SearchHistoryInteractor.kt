package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track

interface SearchHistoryInteractor {
    fun saveTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}
