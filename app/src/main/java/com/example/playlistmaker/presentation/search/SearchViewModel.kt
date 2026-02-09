package com.example.playlistmaker.presentation.search

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

    private val _state = MutableLiveData(SearchScreenState())
    val state: LiveData<SearchScreenState> = _state

    private val _events = MutableLiveData<SearchEvent>()
    val events: LiveData<SearchEvent> = _events

    fun onSearchClicked(query: String) {
        if (query.isBlank()) {
            showHistory()
            return
        }

        _state.value = SearchScreenState(
            isLoading = true,
            isHistory = false,
            isError = false,
            isNoConnection = false
        )

        searchInteractor.searchTracks(
            query = query,
            onResult = { tracks ->
                _state.postValue(
                    SearchScreenState(
                        tracks = tracks,
                        isHistory = false,
                        isLoading = false,
                        isError = false,
                        isNoConnection = false
                    )
                )
            },
            onError = {
                _state.postValue(
                    SearchScreenState(
                        tracks = emptyList(),
                        isHistory = false,
                        isLoading = false,
                        isError = true,
                        isNoConnection = false
                    )
                )
            },
            onNetworkError = {
                _state.postValue(
                    SearchScreenState(
                        tracks = emptyList(),
                        isHistory = false,
                        isLoading = false,
                        isError = false,
                        isNoConnection = true
                    )
                )
            }
        )
    }


    sealed class SearchEvent {
        data class OpenPlayer(val track: Track) : SearchEvent()
    }

    fun showHistory() {
        val history = historyInteractor.getHistory()

        _state.value = SearchScreenState(
            tracks = history,
            isHistory = true,
            isLoading = false,
            isError = false,
            isNoConnection = false
        )
    }

    fun clearHistory() {
        historyInteractor.clearHistory()

        _state.value = SearchScreenState(
            tracks = emptyList(),
            isHistory = true,
            isLoading = false,
            isError = false,
            isNoConnection = false
        )
    }

    fun onTrackClicked(track: Track) {
        historyInteractor.saveTrack(track)
        _events.value = SearchEvent.OpenPlayer(track)
    }

    fun clearEvent() {
        _events.value = null
    }


}
