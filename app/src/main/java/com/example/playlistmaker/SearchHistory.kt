package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private val gson = Gson()
    fun saveTrack(track: Track) {
        val list = getHistory().toMutableList()

        list.removeAll { it.trackId == track.trackId }
        list.add(0, track)

        if (list.size > MAX_SIZE) {
            list.removeAt(list.lastIndex)
        }

        val json = gson.toJson(list)
        sharedPrefs.edit().putString(KEY_HISTORY, json).apply()
    }

    fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(KEY_HISTORY, null) ?: return emptyList()

        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clearHistory() {
        sharedPrefs.edit().remove(KEY_HISTORY).apply()
    }

    companion object {
        private const val KEY_HISTORY = "history"
        private const val MAX_SIZE = 10
    }

}
