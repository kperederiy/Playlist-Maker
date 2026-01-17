package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar

class AudioPlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    private lateinit var btnPlay: ImageView
    private var mediaPlayer = MediaPlayer()
    private lateinit var durationTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentPosition = mediaPlayer.currentPosition
                durationTextView.text = formatTime(currentPosition)
                handler.postDelayed(this, 300)
            }
        }
    }
    private fun formatTime(millis: Int): String {
        val minutes = millis / 1000 / 60
        val seconds = millis / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

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

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

        Glide.with(this).load(track.getCoverArtwork()).into(cover)
        trackName.text = track.trackName
        trackTime.text = track.trackTime
        artistName.text = track.artistName
        collection.text = track.collectionName
        genre.text = track.primaryGenreName
        country.text = track.country
        releaseDate.text = track.releaseDate.take(4)

        btnPlay = findViewById(R.id.btnPlay)
        preparePlayer(track.previewUrl)

        btnPlay.setOnClickListener {
            playbackControl()
        }
        durationTextView = findViewById(R.id.duration)
        durationTextView.text = "00:00"

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
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    // ---------- ЛОГИКА УПРАВЛЕНИЯ ----------

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    // ---------- ИНИЦИАЛИЗАЦИЯ ----------

    private fun preparePlayer(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            btnPlay.isEnabled = true
        }

        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(updateProgressRunnable)
            playerState = STATE_PREPARED
            btnPlay.setImageResource(R.drawable.ic_play_100)
            durationTextView.text = "00:00"
        }
    }

    // ---------- УПРАВЛЕНИЕ ----------

    private fun startPlayer() {
        mediaPlayer.start()
        btnPlay.setImageResource(R.drawable.ic_pause_100)
        playerState = STATE_PLAYING
        handler.post(updateProgressRunnable)
    }

    private fun pausePlayer() {
        if (playerState == STATE_PLAYING) {
            mediaPlayer.pause()
            handler.removeCallbacks(updateProgressRunnable)
        }
        btnPlay.setImageResource(R.drawable.ic_play_100)
        playerState = STATE_PAUSED
    }

}
fun Track.getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")