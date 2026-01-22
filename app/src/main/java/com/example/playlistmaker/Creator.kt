package com.example.playlistmaker

import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractorImpl
import com.example.playlistmaker.domain.repository.TracksRepository

object Creator {

    private fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitClient.iTunesService)
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(provideTracksRepository())
    }
}

