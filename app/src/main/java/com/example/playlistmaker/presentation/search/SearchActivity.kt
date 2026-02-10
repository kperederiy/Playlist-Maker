package com.example.playlistmaker.presentation.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.player.AudioPlayerActivity
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.widget.doOnTextChanged

private const val CLICK_DEBOUNCE_DELAY = 1000L

class SearchActivity : AppCompatActivity() {
    private lateinit var inputSearchText: EditText
    private lateinit var btnClearSearch: ImageView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var errorState: LinearLayout
    private lateinit var btnRetry: View
    private var currentText: String = ""
    private val tracks: MutableList<Track> = mutableListOf()
    private val tracksAdapter = TrackAdapter(tracks)

    private val searchHistoryInteractor by lazy {
        Creator.provideSearchHistoryInteractor(applicationContext)
    }
    private lateinit var historyTitle: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var btnClearHistory: View

    private val searchHandler = Handler(Looper.getMainLooper())
    private lateinit var progressBar: View
    private var isClickAllowed = true

    private val viewModel by viewModels<SearchViewModel> {
        SearchViewModelFactory(
            Creator.provideSearchInteractor(),
            Creator.provideSearchHistoryInteractor(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val rootView = findViewById<View>(R.id.root)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }

        inputSearchText = findViewById(R.id.inputSearchText)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        tracksRecyclerView = findViewById(R.id.Tracks)
        emptyState = findViewById(R.id.emptyState)
        errorState = findViewById(R.id.errorState)
        btnRetry = findViewById(R.id.btnRetry)
        progressBar = findViewById(R.id.progressBar)

        viewModel.observeState().observe(this) { state ->
            render(state)
        }

        tracksRecyclerView.adapter = tracksAdapter
        tracksAdapter.onTrackClick = {
            viewModel.onTrackClicked(it)
            openPlayer(it)
        }

        historyTitle = findViewById(R.id.historyTitle)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        btnClearHistory = findViewById(R.id.btnClearHistory)

        historyAdapter = TrackAdapter(mutableListOf())
        historyRecyclerView.adapter = historyAdapter
        historyAdapter.onTrackClick = { track ->
            if (clickDebounce()) {
                openPlayer(track)
            }
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        inputSearchText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputSearchText.text.isEmpty()) {
                updateHistory()
            }
        }

        inputSearchText.doOnTextChanged { text, _, _, _ ->
            viewModel.onSearchTextChanged(text.toString())
        }

        inputSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                if (currentText.isNotEmpty()) {
                    searchTracks(currentText)
                }

                // Скрываем клавиатуру
                val imm = getSystemService<InputMethodManager>()
                imm?.hideSoftInputFromWindow(inputSearchText.windowToken, 0)

                true
            } else {
                false
            }
        }

        btnClearSearch.setOnClickListener {
            inputSearchText.text.clear()
            btnClearSearch.visibility = View.GONE

            // Скрываем клавиатуру
            val imm = getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(inputSearchText.windowToken, 0)
        }

        btnRetry.setOnClickListener {
            viewModel.onRetry()
        }

        btnClearHistory.setOnClickListener {
            viewModel.onClearHistory()
        }

    }

    private fun render(state: SearchState) {
        progressBar.isVisible = state.isLoading
        errorState.isVisible = state.isError
        emptyState.isVisible = state.isEmpty

        tracksRecyclerView.isVisible = state.tracks.isNotEmpty()
        historyTitle.isVisible = state.showHistory
        historyRecyclerView.isVisible = state.showHistory
        btnClearHistory.isVisible = state.showHistory

        btnClearSearch.isVisible = state.showClearButton

        tracksAdapter.updateItems(state.tracks)
        historyAdapter.updateItems(state.history)
    }

    private fun updateHistory() {
        val history = searchHistoryInteractor.getHistory()
        historyAdapter.updateItems(history)

        val isHistoryEmpty = history.isEmpty()

        historyTitle.isVisible = !isHistoryEmpty
        historyRecyclerView.isVisible = !isHistoryEmpty
        btnClearHistory.isVisible = !isHistoryEmpty
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search_text", currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString("search_text", "")
        inputSearchText.setText(restoredText)
    }

    private val searchInteractor by lazy {
        Creator.provideSearchInteractor()
    }

    private fun searchTracks(query: String) {
        showLoading()

        searchInteractor.searchTracks(
            query = query,
            onResult = { tracks ->
                runOnUiThread {
                    hideLoading()

                    if (tracks.isEmpty()) {
                        showEmpty()
                    } else {
                        showTracks(tracks)
                    }
                }
            },
            onError = {
                runOnUiThread {
                    hideLoading()
                    showError()
                }
            }
        )
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        tracksRecyclerView.visibility = View.GONE
        emptyState.visibility = View.GONE
        errorState.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showTracks(tracks: List<Track>) {
        tracksAdapter.updateItems(tracks)
        tracksRecyclerView.visibility = View.VISIBLE
        emptyState.visibility = View.GONE
        errorState.visibility = View.GONE
    }

    private fun showEmpty() {
        emptyState.visibility = View.VISIBLE
        tracksRecyclerView.visibility = View.GONE
        errorState.visibility = View.GONE
    }

    private fun showError() {
        errorState.visibility = View.VISIBLE
        tracksRecyclerView.visibility = View.GONE
        emptyState.visibility = View.GONE
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            searchHandler.postDelayed(
                { isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }
    override fun onDestroy() {
        super.onDestroy()
        searchHandler.removeCallbacksAndMessages(null)
    }

}


