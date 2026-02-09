package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.model.Track

data class SearchScreenState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isNoConnection: Boolean = false,
    val isHistory: Boolean = false
)
