package com.example.playlistmaker.presentation.player

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: AudioPlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track = intent.getSerializableExtra("track") as Track

        val cover = findViewById<ImageView>(R.id.cover)
        val trackName = findViewById<TextView>(R.id.trackName)
        val trackTime = findViewById<TextView>(R.id.trackTime)
        val artistName = findViewById<TextView>(R.id.artistName)
        val collection = findViewById<TextView>(R.id.collectionName)
        val genre = findViewById<TextView>(R.id.primaryGenreName)
        val country = findViewById<TextView>(R.id.country)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val btnPlay = findViewById<ImageView>(R.id.btnPlay)
        val durationTextView = findViewById<TextView>(R.id.duration)

        Glide.with(this).load(track.getCoverArtwork()).into(cover)
        trackName.text = track.trackName
        trackTime.text = track.trackTime
        artistName.text = track.artistName
        collection.text = track.collectionName
        genre.text = track.primaryGenreName
        country.text = track.country
        releaseDate.text = track.releaseDate.take(4)

        viewModel = ViewModelProvider(
            this,
            Creator.provideAudioPlayerViewModelFactory()
        )[AudioPlayerViewModel::class.java]

        viewModel.state.observe(this) { state ->
            btnPlay.isEnabled = state.isPlayButtonEnabled
            durationTextView.text = state.currentTime
            btnPlay.setImageResource(
                if (state.isPlaying)
                    R.drawable.ic_pause_100
                else
                    R.drawable.ic_play_100
            )
        }

        btnPlay.setOnClickListener {
            viewModel.onPlayClicked()
        }

        viewModel.prepare(track.previewUrl)
    }
}
fun Track.getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
