package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.library.FavoriteTracksViewModel
import com.example.playlistmaker.presentation.playlists.PlaylistsViewModel
import com.example.playlistmaker.presentation.player.AudioPlayerViewModel
import com.example.playlistmaker.presentation.playlists.NewPlaylistViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(
            get(),
            get()
        )
    }

    viewModel {
        AudioPlayerViewModel(
            get(),
            get()
        )
    }

    viewModel {
        SettingsViewModel(
            get()
        )
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }
}

