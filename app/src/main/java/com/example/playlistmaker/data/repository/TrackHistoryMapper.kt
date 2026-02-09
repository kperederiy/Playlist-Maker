package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.storage.TrackHistoryDto
import com.example.playlistmaker.domain.model.Track

fun Track.toDto() = TrackHistoryDto(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl
)

fun TrackHistoryDto.toDomain() = Track(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTime,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl
)
