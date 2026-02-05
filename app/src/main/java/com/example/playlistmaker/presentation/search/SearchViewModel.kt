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

    fun onSearchClicked(query: String) {
        if (query.isBlank()) {
            showHistory()
            return
        }

        _state.value = SearchScreenState(isLoading = true)

        searchInteractor.searchTracks(
            query = query,
            onResult = { tracks ->
                _state.postValue(
                    SearchScreenState(
                        tracks = tracks,
                        isHistory = false
                    )
                )
            },
            onError = {
                _state.postValue(
                    SearchScreenState(isError = true)
                )
            }
        )
    }


    fun showHistory() {
        val history = historyInteractor.getHistory()

        _state.value = SearchScreenState(
            tracks = history,
            isHistory = true
        )
    }

    fun onTrackClicked(track: Track) {
        historyInteractor.saveTrack(track)
    }

}
