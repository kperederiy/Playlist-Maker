package com.example.playlistmaker.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.library.LibraryActivity
import com.example.playlistmaker.presentation.search.SearchActivity
import com.example.playlistmaker.presentation.settings.SettingsActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rootView = findViewById<View>(R.id.root)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }

        val buttonSearch = findViewById<MaterialButton>(R.id.search)

        buttonSearch.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        val buttonLibrary = findViewById<MaterialButton>(R.id.library)

        buttonLibrary.setOnClickListener {
            val libraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }

        val buttonSettings = findViewById<MaterialButton>(R.id.settings)

        buttonSettings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}