package com.example.playlistmaker

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputSearchText = findViewById(R.id.inputSearchText)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        tracksRecyclerView = findViewById(R.id.Tracks)
        emptyState = findViewById(R.id.emptyState)
        errorState = findViewById(R.id.errorState)
        btnRetry = findViewById(R.id.btnRetry)


        tracksRecyclerView.adapter = tracksAdapter
        tracksAdapter.onTrackClick = { track ->
            searchHistory.saveTrack(track)
            updateHistory()
            Toast.makeText(
                this,
                "Трек с ID ${track.trackId} добавлен в историю",
                Toast.LENGTH_SHORT
            ).show()
        }

        searchHistory = SearchHistory(getSharedPreferences("history_prefs", MODE_PRIVATE))
        historyTitle = findViewById(R.id.historyTitle)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        btnClearHistory = findViewById(R.id.btnClearHistory)

        // обязательно установить layoutManager прежде чем присваивать адаптер
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        historyAdapter = TrackAdapter(mutableListOf())
        historyRecyclerView.adapter = historyAdapter


        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        inputSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClearSearch.isVisible = !s.isNullOrEmpty()
                currentText = s.toString()

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

                    val history = searchHistory.getHistory()
                    if (history.isNotEmpty()) {
                        val message = history.joinToString("\n") { track ->
                            "• ${track.trackName} (ID: ${track.trackId})"
                        }

                        Toast.makeText(
                            this@SearchActivity,
                            "История:\n$message",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@SearchActivity,
                            "История пустая",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

        iTunesService.searchSongs(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
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
                                artworkUrl100 = result.artworkUrl100
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
                tracksRecyclerView.visibility = View.GONE
                emptyState.visibility = View.GONE
                errorState.visibility = View.VISIBLE
            }
        })
    }

}

