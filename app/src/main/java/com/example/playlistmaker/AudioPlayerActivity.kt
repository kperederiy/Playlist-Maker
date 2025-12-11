package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class AudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track = intent.getSerializableExtra("track") as Track

        val cover = findViewById<ImageView>(R.id.cover)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val collection = findViewById<TextView>(R.id.collection)
        val genre = findViewById<TextView>(R.id.genre)
        val country = findViewById<TextView>(R.id.country)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val back = findViewById<ImageView>(R.id.btnBack)

        Glide.with(this).load(track.artworkUrl100).into(cover)
        trackName.text = track.trackName
        artistName.text = track.artistName
        collection.text = track.collectionName
        genre.text = track.primaryGenreName
        country.text = track.country
        releaseDate.text = track.releaseDate.take(10) // yyyy-MM-dd

        back.setOnClickListener { finish() }
    }
}
