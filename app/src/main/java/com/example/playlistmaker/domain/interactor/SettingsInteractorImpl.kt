package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        return repository.isDarkThemeEnabled()
    }

    override fun switchTheme(enabled: Boolean) {
        repository.setDarkTheme(enabled)
    }
}
