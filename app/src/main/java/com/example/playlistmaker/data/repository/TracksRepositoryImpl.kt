package com.example.playlistmaker.data.repository

import android.util.Log
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.domain.Resource
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val api: ITunesApi
) : TracksRepository {

    override fun searchTracks(query: String): Flow<Resource<List<Track>>> = flow {

        emit(Resource.Loading())

        val response = api.searchSongs(query)

        val tracks = response.results.map { dto ->
            Track(
                trackId = dto.trackId,
                trackName = dto.trackName,
                artistName = dto.artistName,
                trackTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(dto.trackTimeMillis),
                artworkUrl100 = dto.artworkUrl100,
                collectionName = dto.collectionName ?: "",
                releaseDate = dto.releaseDate?.take(4) ?: "",
                primaryGenreName = dto.primaryGenreName,
                country = dto.country,
                previewUrl = dto.previewUrl.orEmpty()
            )
        }

        emit(Resource.Success(tracks))

    }.catch { throwable ->
        Log.e("SEARCH_ERROR", throwable.message ?: "error")
        Log.e("SEARCH_ERROR", throwable::class.java.name)
        emit(Resource.Error(throwable))
    }
}
