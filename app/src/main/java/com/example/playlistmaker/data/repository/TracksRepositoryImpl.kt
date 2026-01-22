package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.network.SearchResponseDto
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val api: ITunesApi
) : TracksRepository {

    override fun searchTracks(query: String, callback: (List<Track>) -> Unit) {
        api.searchSongs(query).enqueue(object : Callback<SearchResponseDto> {
            override fun onResponse(
                call: Call<SearchResponseDto>,
                response: Response<SearchResponseDto>
            ) {
                val tracks = response.body()?.results?.map { dto ->
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
                        previewUrl = dto.previewUrl
                    )
                } ?: emptyList()

                callback(tracks)
            }

            override fun onFailure(call: Call<SearchResponseDto>, t: Throwable) {
                callback(emptyList())
            }
        })
    }
}
