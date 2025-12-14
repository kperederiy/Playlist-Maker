package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar

class AudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

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

        val btnPlay = findViewById<ImageView>(R.id.btnPlay)
        var isPlaying = false
        btnPlay.setOnClickListener {
            isPlaying = !isPlaying
            if (isPlaying) {
                btnPlay.setImageResource(R.drawable.ic_pause_100)
                // здесь можно добавить логику для запуска воспроизведения
            } else {
                btnPlay.setImageResource(R.drawable.ic_play_100)
                // здесь можно добавить логику для паузы
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

}
fun Track.getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
