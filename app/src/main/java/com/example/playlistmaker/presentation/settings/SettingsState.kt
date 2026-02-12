package com.example.playlistmaker.presentation.settings

data class SettingsState(
    val isDarkThemeEnabled: Boolean,
    val shouldApplyTheme: Boolean = false,
    val shouldShareApp: Boolean = false,
    val shouldOpenSupport: Boolean = false,
    val shouldOpenUserAgreement: Boolean = false
)
