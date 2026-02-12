package com.example.playlistmaker.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.storage.SettingsStorage
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val storage: SettingsStorage,
    private val themeManager: ThemeManager
) : SettingsRepository {

    override fun isDarkThemeEnabled(): Boolean {
        return storage.isDarkThemeEnabled()
    }

    override fun setDarkTheme(enabled: Boolean) {
        storage.setDarkTheme(enabled)
        themeManager.applyTheme(enabled)
    }
}

class ThemeManager(private val context: Context) {

    fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}