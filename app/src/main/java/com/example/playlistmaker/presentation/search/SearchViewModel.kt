package com.example.playlistmaker.presentation.search

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.model.Track

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData(SearchState())
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private var lastQuery: String = ""

    fun onSearchTextChanged(text: String) {
        lastQuery = text

        searchRunnable?.let { handler.removeCallbacks(it) }

        val currentState = stateLiveData.value ?: SearchState()

        if (text.isEmpty()) {
            val history = historyInteractor.getHistory()

            stateLiveData.value = currentState.copy(
                tracks = emptyList(),
                isLoading = false,
                isError = false,
                isEmpty = false,
                history = history,
                showHistory = history.isNotEmpty(),
                showClearButton = false
            )
            return
        }

        stateLiveData.value = currentState.copy(
            showClearButton = true,
            showHistory = false
        )

        searchRunnable = Runnable {
            search(text)
        }

        handler.postDelayed(searchRunnable!!, 2000L)
    }

    private fun search(query: String) {
        val currentState = stateLiveData.value ?: SearchState()

        stateLiveData.value = currentState.copy(
            isLoading = true,
            isError = false,
            isEmpty = false,
            showHistory = false
        )

        searchInteractor.searchTracks(
            query = query,
            onResult = { tracks ->
                if (tracks.isEmpty()) {
                    stateLiveData.postValue(
                        stateLiveData.value?.copy(
                            isLoading = false,
                            isEmpty = true,
                            tracks = emptyList()
                        )
                    )
                } else {
                    stateLiveData.postValue(
                        stateLiveData.value?.copy(
                            isLoading = false,
                            tracks = tracks,
                            isEmpty = false
                        )
                    )
                }
            },
            onError = {
                stateLiveData.postValue(
                    stateLiveData.value?.copy(
                        isLoading = false,
                        isError = true
                    )
                )
            }
        )
    }

    fun onTrackClicked(track: Track): Boolean {
        if (!isClickAllowed) return false

        isClickAllowed = false
        handler.postDelayed(
            { isClickAllowed = true },
            1000L
        )

        historyInteractor.saveTrack(track)
        return true
    }


    fun onClearHistory() {
        historyInteractor.clearHistory()

        stateLiveData.value = stateLiveData.value?.copy(
            history = emptyList(),
            showHistory = false
        )
    }

    fun onRetry() {
        if (lastQuery.isNotEmpty()) {
            search(lastQuery)
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }
}
