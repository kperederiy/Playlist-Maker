package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.model.Track

data class SearchScreenState(
    val isLoading: Boolean = false,
    val tracks: List<Track> = emptyList(),
    val isHistory: Boolean = false,
    val isError: Boolean = false
)

