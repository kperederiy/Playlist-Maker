package com.example.playlistmaker.domain.repository

interface SettingsRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkTheme(enabled: Boolean)
}
