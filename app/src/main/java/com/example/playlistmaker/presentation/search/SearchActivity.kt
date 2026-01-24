package com.example.playlistmaker.presentation.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
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
import androidx.core.view.isGone

private const val SEARCH_DEBOUNCE_DELAY = 2000L
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
    private var searchRunnable: Runnable? = null
    private lateinit var progressBar: View
    private var isClickAllowed = true

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


        tracksRecyclerView.adapter = tracksAdapter
        tracksAdapter.onTrackClick = { track ->
            if (clickDebounce()) {
                searchHistoryInteractor.saveTrack(track)
                updateHistory()
                openPlayer(track)
            }
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

        inputSearchText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputSearchText.text.isEmpty()) {
                historyTitle.visibility = View.VISIBLE
                historyRecyclerView.visibility = View.VISIBLE
                btnClearHistory.visibility = View.VISIBLE
                updateHistory()
            }
        }

        inputSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClearSearch.isVisible = !s.isNullOrEmpty()
                currentText = s.toString()

                searchRunnable?.let {
                    searchHandler.removeCallbacks(it)
                }

                if (currentText.isEmpty()) {
                    tracks.clear()
                    tracksAdapter.notifyDataSetChanged()

                    tracksRecyclerView.visibility = View.GONE
                    emptyState.visibility = View.GONE
                    errorState.visibility = View.GONE

                    historyTitle.visibility = View.VISIBLE
                    historyRecyclerView.visibility = View.VISIBLE
                    btnClearHistory.visibility = View.VISIBLE
                    updateHistory()
                } else {
                    historyTitle.visibility = View.GONE
                    historyRecyclerView.visibility = View.GONE
                    btnClearHistory.visibility = View.GONE

                    searchRunnable = Runnable {
                        searchTracks(currentText)
                    }
                    searchHandler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
                }

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

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
            if (currentText.isNotEmpty()) {
                searchTracks(currentText)
            }
        }

        btnClearHistory.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            updateHistory()
        }

    }

    private fun updateHistory() {
        val history = searchHistoryInteractor.getHistory()
        historyAdapter.updateItems(history)

        if (tracksRecyclerView.isGone) tracksRecyclerView.visibility = View.GONE

        if (historyTitle.isGone) historyTitle.visibility = View.GONE
        if (historyRecyclerView.isGone) historyRecyclerView.visibility = View.GONE
        if (btnClearHistory.isGone) btnClearHistory.visibility = View.GONE
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


