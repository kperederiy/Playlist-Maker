package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)
    interface ITunesApi {
        @GET("search?entity=song")
        fun searchSongs(@Query("term") text: String): Call<SearchResponse>
    }

    private lateinit var inputSearchText: EditText
    private lateinit var btnClearSearch: ImageView

    private var currentText: String = ""
    private val tracks: MutableList<Track> = mutableListOf()
    private val tracksAdapter = TrackAdapter(tracks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputSearchText = findViewById(R.id.inputSearchText)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        val tracksRecyclerView : RecyclerView = findViewById(R.id.Tracks)

        tracksRecyclerView.adapter = tracksAdapter

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

                if (currentText.isNotEmpty()) {
                    searchTracks(currentText)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        btnClearSearch.setOnClickListener {
            inputSearchText.text.clear()
            btnClearSearch.visibility = View.GONE

            // Скрываем клавиатуру
            val imm = getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(inputSearchText.windowToken, 0)
        }
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
                    Toast.makeText(this@SearchActivity, "Треков нашли: ${body?.results?.size}", Toast.LENGTH_SHORT).show()
                    tracks.clear()
                    body?.results?.forEach { result ->
                        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                            .format(result.trackTimeMillis)
                        tracks.add(
                            Track(
                                trackName = result.trackName,
                                artistName = result.artistName,
                                trackTime = formattedTime,
                                artworkUrl100 = result.artworkUrl100
                            )
                        )
                    }
                    tracksAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                //t.printStackTrace()
                Toast.makeText(this@SearchActivity, "Ошибка сети: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}