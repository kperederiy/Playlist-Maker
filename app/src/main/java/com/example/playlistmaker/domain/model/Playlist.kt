package com.example.playlistmaker.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Playlist(

    val id: Long = 0,
    val name: String,
    val description: String,
    val coverPath: String,
    val trackIds: List<Int>,
    val tracksCount: Int
) : Parcelable