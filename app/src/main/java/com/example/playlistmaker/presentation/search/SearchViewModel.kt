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

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private var lastQuery: String = ""

    fun onSearchTextChanged(text: String) {
        lastQuery = text

        searchRunnable?.let { handler.removeCallbacks(it) }

        if (text.isEmpty()) {
            val history = historyInteractor.getHistory()
            stateLiveData.value = SearchState(
                history = history,
                showHistory = history.isNotEmpty(),
                showClearButton = false
            )
            return
        }

        stateLiveData.value = stateLiveData.value?.copy(
            showClearButton = true
        )

        searchRunnable = Runnable {
            search(text)
        }
        handler.postDelayed(searchRunnable!!, 2000L)
    }


    fun search(query: String) {
        stateLiveData.value = stateLiveData.value?.copy(
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
                            tracks = tracks
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

    fun onTrackClicked(track: Track) {
        historyInteractor.saveTrack(track)
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
