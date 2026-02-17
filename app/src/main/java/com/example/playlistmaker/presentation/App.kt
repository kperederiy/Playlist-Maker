package com.example.playlistmaker.presentation

import android.app.Application
import com.example.playlistmaker.data.repository.ThemeManager
import com.example.playlistmaker.data.storage.SettingsStorage
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                interactorModule,
                viewModelModule
            )
        }

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val storage = SettingsStorage(prefs)
        val themeManager = ThemeManager(this)

        val isDark = storage.isDarkThemeEnabled()
        themeManager.applyTheme(isDark)
    }
}
