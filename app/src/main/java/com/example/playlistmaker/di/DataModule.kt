package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.repository.ThemeManager
import com.example.playlistmaker.data.storage.SearchHistoryStorage
import com.example.playlistmaker.data.storage.SettingsStorage
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    // SharedPreferences
    single {
        androidContext()
            .getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    // Gson
    factory { Gson() }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API
    single<ITunesApi> {
        get<Retrofit>().create(ITunesApi::class.java)
    }

    // Storage
    single {
        SearchHistoryStorage(
            sharedPrefs = get(),
            gson = get()
        )
    }

    single {
        SettingsStorage(get())
    }

    // ThemeManager
    single {
        ThemeManager(androidContext())
    }

    // MediaPlayer
    factory {
        MediaPlayer()
    }

    single<AppDatabase> {
        Room.databaseBuilder(
            get<Context>(),
            AppDatabase::class.java,
            "playlist_database"
        ).build()
    }

    single {
        get<AppDatabase>().favoriteTrackDao()
    }
}
