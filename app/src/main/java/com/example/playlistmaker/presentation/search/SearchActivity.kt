package com.example.playlistmaker.presentation.search

import android.content.Intent
import android.os.Bundle
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

class SearchActivity : AppCompatActivity() {
    private lateinit var inputSearchText: EditText
    private lateinit var btnClearSearch: ImageView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var errorState: LinearLayout
    private lateinit var btnRetry: View
    private lateinit var progressBar: View

    private lateinit var historyTitle: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var btnClearHistory: View

    private val tracksAdapter = TrackAdapter(mutableListOf())
    private val historyAdapter = TrackAdapter(mutableListOf())

    private var currentText: String = ""

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
        tracksAdapter.onTrackClick = { track ->
            viewModel.onTrackClicked(track)
            openPlayer(track)
        }


        historyTitle = findViewById(R.id.historyTitle)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        btnClearHistory = findViewById(R.id.btnClearHistory)

        historyRecyclerView.adapter = historyAdapter
        historyAdapter.onTrackClick = { track ->
            viewModel.onTrackClicked(track)
            openPlayer(track)
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        inputSearchText.doOnTextChanged { text, _, _, _ ->
            currentText = text.toString()
            viewModel.onSearchTextChanged(currentText)
        }

        inputSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = getSystemService<InputMethodManager>()
                imm?.hideSoftInputFromWindow(inputSearchText.windowToken, 0)
                true
            } else {
                false
            }
        }

        btnClearSearch.setOnClickListener {
            inputSearchText.text.clear()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search_text", currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString("search_text", "")
        inputSearchText.setText(restoredText)
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}


