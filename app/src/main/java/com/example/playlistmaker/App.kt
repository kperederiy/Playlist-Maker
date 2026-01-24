package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        applyTheme(isSystemInDarkTheme())
    }

    fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun isSystemInDarkTheme(): Boolean =
        resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}
