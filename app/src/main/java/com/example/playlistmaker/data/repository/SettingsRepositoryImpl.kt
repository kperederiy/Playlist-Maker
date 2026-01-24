package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.storage.SettingsStorage
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val storage: SettingsStorage
) : SettingsRepository {

    override fun isDarkThemeEnabled(): Boolean {
        return storage.isDarkThemeEnabled()
    }

    override fun setDarkTheme(enabled: Boolean) {
        storage.setDarkTheme(enabled)
    }
}
