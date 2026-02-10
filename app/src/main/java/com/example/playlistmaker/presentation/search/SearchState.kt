package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.model.Track

data class SearchState(
    val isLoading: Boolean = false,
    val tracks: List<Track> = emptyList(),
    val history: List<Track> = emptyList(),
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val showHistory: Boolean = false,
    val showClearButton: Boolean = false
)
