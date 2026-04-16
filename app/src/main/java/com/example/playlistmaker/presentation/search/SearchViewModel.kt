package com.example.playlistmaker.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.Resource
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.model.Track
import debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData(SearchState())
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val searchDebounce = debounce<String>(
        delayMillis = 2000L,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { query ->
        search(query)
    }

    private val clickDebounce = debounce<Track>(
        delayMillis = 1000L,
        coroutineScope = viewModelScope,
        useLastParam = false
    ) { track ->
        historyInteractor.saveTrack(track)
    }

    private var lastQuery: String = ""

    fun onSearchTextChanged(text: String) {
        lastQuery = text

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

        searchDebounce(text)
    }

    private fun search(query: String) {

        viewModelScope.launch {
            searchInteractor.searchTracks(query)
                .collect { resource ->

                    when (resource) {

                        is Resource.Loading -> {
                            stateLiveData.postValue(
                                stateLiveData.value?.copy(
                                    isLoading = true,
                                    isError = false,
                                    isEmpty = false,
                                    showHistory = false
                                )
                            )
                        }

                        is Resource.Success -> {
                            val tracks = resource.data

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
                        }

                        is Resource.Error -> {
                            stateLiveData.postValue(
                                stateLiveData.value?.copy(
                                    isLoading = false,
                                    isError = true
                                )
                            )
                        }
                    }
                }
        }
    }

    fun onSearchFieldFocused() {
        val currentState = stateLiveData.value ?: SearchState()

        // Показываем историю только если текст пустой
        if (lastQuery.isEmpty()) {
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
        }
    }

    fun onTrackClicked(track: Track): Boolean {
        clickDebounce(track)
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
    }
}
