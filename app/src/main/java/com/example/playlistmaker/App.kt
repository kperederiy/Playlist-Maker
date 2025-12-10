package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_DARK_THEME = "dark_theme"
    }

    var darkTheme: Boolean = false
        private set

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        switchTheme(prefs.getBoolean(KEY_DARK_THEME, isSystemInDarkTheme()))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_DARK_THEME, darkThemeEnabled)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
    private fun isSystemInDarkTheme(): Boolean =
        resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}
