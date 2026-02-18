package com.example.playlistmaker.presentation.player

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.google.android.material.appbar.MaterialToolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerActivity : AppCompatActivity() {

    private val viewModel: AudioPlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val rootView = findViewById<View>(R.id.root)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }

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

        val btnPlaylist = findViewById<ImageView>(R.id.btnPlaylist)

        var isInPlaylist = false
        btnPlaylist.setOnClickListener {
            isInPlaylist = !isInPlaylist
            if (isInPlaylist) {
                btnPlaylist.setImageResource(R.drawable.ic_del_playlist_51)
                // здесь можно добавить логику добавления трека в плейлист
            } else {
                btnPlaylist.setImageResource(R.drawable.ic_add_playlist_51)
                // здесь можно добавить логику удаления трека из плейлиста
            }
        }
        val btnLike = findViewById<ImageView>(R.id.btnLike)
        var isLiked = false
        btnLike.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                btnLike.setImageResource(R.drawable.ic_del_favorite_51)
                // здесь можно добавить логику для сохранения лайка
            } else {
                btnLike.setImageResource(R.drawable.ic_add_favorite_51)
                // здесь можно добавить логику для удаления лайка
            }
        }
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
fun Track.getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
