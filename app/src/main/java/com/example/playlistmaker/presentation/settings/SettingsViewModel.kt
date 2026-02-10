package com.example.playlistmaker.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactor.SettingsInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val _state = MutableLiveData<SettingsState>()
    val state: LiveData<SettingsState> = _state

    init {
        val isDark = settingsInteractor.isDarkThemeEnabled()
        _state.value = SettingsState(isDarkThemeEnabled = isDark)
    }

    fun onThemeSwitchClicked(isChecked: Boolean) {
        settingsInteractor.switchTheme(isChecked)
        _state.value = _state.value?.copy(
            isDarkThemeEnabled = isChecked,
            shouldApplyTheme = true
        )
    }

    fun onThemeApplied() {
        _state.value = _state.value?.copy(shouldApplyTheme = false)
    }

    fun onShareClicked() {
        _state.value = _state.value?.copy(shouldShareApp = true)
    }

    fun onSupportClicked() {
        _state.value = _state.value?.copy(shouldOpenSupport = true)
    }

    fun onUserAgreementClicked() {
        _state.value = _state.value?.copy(shouldOpenUserAgreement = true)
    }

    fun onActionHandled() {
        _state.value = _state.value?.copy(
            shouldShareApp = false,
            shouldOpenSupport = false,
            shouldOpenUserAgreement = false
        )
    }
}


