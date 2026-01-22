package com.example.playlistmaker.data.network

data class SearchResponseDto(
    val resultCount: Int,
    val results: List<TrackDto>
)
