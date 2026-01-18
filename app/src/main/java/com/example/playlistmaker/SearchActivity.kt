package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
private const val SEARCH_DEBOUNCE_DELAY = 2000L
private const val CLICK_DEBOUNCE_DELAY = 1000L

class SearchActivity : AppCompatActivity() {
    private val iTunesService = RetrofitClient.iTunesService
    private lateinit var inputSearchText: EditText
    private lateinit var btnClearSearch: ImageView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var errorState: LinearLayout
    private lateinit var btnRetry: View
    private var currentText: String = ""
    private val tracks: MutableList<Track> = mutableListOf()
    private val tracksAdapter = TrackAdapter(tracks)

    private lateinit var searchHistory: SearchHistory
    private lateinit var historyTitle: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var btnClearHistory: View

    private val searchHandler = android.os.Handler(android.os.Looper.getMainLooper())
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
                searchHistory.saveTrack(track)
                updateHistory()
                openPlayer(track)
            }
        }

        searchHistory = SearchHistory(getSharedPreferences("history_prefs", MODE_PRIVATE))
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
            searchHistory.clearHistory()
            updateHistory()
        }

    }

    private fun updateHistory() {
        val list = searchHistory.getHistory()
        if (list.isEmpty()) {
            // скрываем, если пусто
            historyTitle.visibility = View.GONE
            historyRecyclerView.visibility = View.GONE
            btnClearHistory.visibility = View.GONE
        }
        historyAdapter.updateItems(list)
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


    private fun searchTracks(query: String) {
        progressBar.visibility = View.VISIBLE
        tracksRecyclerView.visibility = View.GONE
        emptyState.visibility = View.GONE
        errorState.visibility = View.GONE

        iTunesService.searchSongs(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val body = response.body()
                    tracks.clear()
                    body?.results?.forEach { result ->
                        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                            .format(result.trackTimeMillis)
                        tracks.add(
                            Track(
                                trackId = result.trackId,
                                trackName = result.trackName,
                                artistName = result.artistName,
                                trackTime = formattedTime,
                                artworkUrl100 = result.artworkUrl100,
                                collectionName = result.collectionName ?: "",
                                releaseDate = result.releaseDate ?: "",
                                primaryGenreName = result.primaryGenreName,
                                country = result.country,
                                previewUrl = result.previewUrl
                            )
                        )
                    }
                    tracksAdapter.notifyDataSetChanged()
                    historyRecyclerView.visibility = View.GONE
                    btnClearHistory.visibility = View.GONE
                    if (tracks.isEmpty()) {
                        tracksRecyclerView.visibility = View.GONE
                        emptyState.visibility = View.VISIBLE
                        errorState.visibility = View.GONE
                    } else {
                        emptyState.visibility = View.GONE
                        errorState.visibility = View.GONE
                        tracksRecyclerView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                tracksRecyclerView.visibility = View.GONE
                emptyState.visibility = View.GONE
                errorState.visibility = View.VISIBLE
            }
        })
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


