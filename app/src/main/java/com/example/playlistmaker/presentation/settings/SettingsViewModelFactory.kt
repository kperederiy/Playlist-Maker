package com.example.playlistmaker.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.interactor.SettingsInteractor

class SettingsViewModelFactory(
    private val settingsInteractor: SettingsInteractor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(settingsInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}