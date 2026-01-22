package com.example.playlistmaker.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorage(
    private val sharedPrefs: SharedPreferences
) {
    private val gson = Gson()

    fun save(tracks: List<TrackHistoryDto>) {
        sharedPrefs.edit()
            .putString(KEY_HISTORY, gson.toJson(tracks))
            .apply()
    }

    fun get(): List<TrackHistoryDto> {
        val json = sharedPrefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val type = object : TypeToken<List<TrackHistoryDto>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clear() {
        sharedPrefs.edit().remove(KEY_HISTORY).apply()
    }

    companion object {
        private const val KEY_HISTORY = "history"
    }
}
