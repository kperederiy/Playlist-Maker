package com.example.playlistmaker.presentation.root

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.MainFragment

class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val rootView = findViewById<View>(R.id.rootFragmentContainerView)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(
                    R.id.rootFragmentContainerView,
                    MainFragment()
                )
            }
        }
    }
}