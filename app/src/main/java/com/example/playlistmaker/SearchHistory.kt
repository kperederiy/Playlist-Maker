package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private val gson = Gson()
    private val KEY_HISTORY = "search_history"
    private val MAX_SIZE = 10

    // ???? Универсальная функция для чтения списков без TypeToken в методах
    private inline fun <reified T> fromJson(json: String): T =
        gson.fromJson(json, object : TypeToken<T>() {}.type)

    fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return fromJson(json)
    }

    fun saveTrack(track: Track) {
        val list = getHistory().toMutableList()

        // Удаляем, если такой trackId уже есть
        list.removeAll { it.trackId == track.trackId }

        // Добавляем в начало
        list.add(0, track)

        // Ограничиваем 10 элементами
        if (list.size > MAX_SIZE) {
            list.removeAt(list.lastIndex)
        }

        sharedPrefs.edit()
            .putString(KEY_HISTORY, gson.toJson(list))
            .apply()
    }

    fun clearHistory() {
        sharedPrefs.edit().remove(KEY_HISTORY).apply()
    }
}
