package com.example.playlistmaker.presentation

import android.app.Application
import com.example.playlistmaker.data.repository.ThemeManager
import com.example.playlistmaker.data.storage.SettingsStorage

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val storage = SettingsStorage(prefs)
        val themeManager = ThemeManager(this)

        val isDark = storage.isDarkThemeEnabled()
        themeManager.applyTheme(isDark)
    }
}
