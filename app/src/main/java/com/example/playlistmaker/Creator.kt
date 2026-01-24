package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.data.storage.SettingsStorage
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractorImpl
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.interactor.SettingsInteractorImpl
import com.example.playlistmaker.domain.repository.TracksRepository

object Creator {

    private fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitClient.iTunesService)
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(provideTracksRepository())
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val prefs = context.getSharedPreferences("history_prefs", Context.MODE_PRIVATE)
        val storage = SearchHistoryStorage(prefs)
        val repository = SearchHistoryRepositoryImpl(storage)
        return SearchHistoryInteractorImpl(repository)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val storage = SettingsStorage(prefs)
        val repository = SettingsRepositoryImpl(storage)
        return SettingsInteractorImpl(repository)
    }

}

