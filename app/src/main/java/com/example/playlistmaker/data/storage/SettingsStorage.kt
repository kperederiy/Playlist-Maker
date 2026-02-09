package com.example.playlistmaker.data.storage

import android.content.SharedPreferences

class SettingsStorage(
    private val sharedPrefs: SharedPreferences
) {
    fun isDarkThemeEnabled(): Boolean {
        return sharedPrefs.getBoolean(KEY_DARK_THEME, false)
    }

    fun setDarkTheme(enabled: Boolean) {
        sharedPrefs.edit()
            .putBoolean(KEY_DARK_THEME, enabled)
            .apply()
    }

    companion object {
        private const val KEY_DARK_THEME = "dark_theme"
    }
}
