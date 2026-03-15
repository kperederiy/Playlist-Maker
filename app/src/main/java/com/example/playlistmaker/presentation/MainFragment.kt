package com.example.playlistmaker.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.library.LibraryFragment
import com.example.playlistmaker.presentation.search.SearchFragment
import com.example.playlistmaker.presentation.settings.SettingsFragment
import com.google.android.material.button.MaterialButton

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_main,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val buttonSearch = view.findViewById<MaterialButton>(R.id.search)
        val buttonLibrary = view.findViewById<MaterialButton>(R.id.library)
        val buttonSettings = view.findViewById<MaterialButton>(R.id.settings)

        buttonSearch.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.rootFragmentContainerView,
                    SearchFragment()
                )
                addToBackStack(null)
            }
        }

        buttonLibrary.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.rootFragmentContainerView,
                    LibraryFragment()
                )
                addToBackStack(null)
            }
        }

        buttonSettings.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.rootFragmentContainerView,
                    SettingsFragment()
                )
                addToBackStack(null)
            }
        }
    }
}