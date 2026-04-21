package com.example.playlistmaker.domain

sealed class Resource<T> {

    data class Success<T>(val data: T) : Resource<T>()

    data class Error<T>(
        val throwable: Throwable,
        val message: String? = null
    ) : Resource<T>()

    class Loading<T> : Resource<T>()
}