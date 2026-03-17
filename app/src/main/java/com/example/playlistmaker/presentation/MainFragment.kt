package com.example.playlistmaker.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
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
            findNavController().navigate(
                R.id.action_mainFragment_to_searchFragment
            )
        }

        buttonLibrary.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainFragment_to_libraryFragment
            )
        }

        buttonSettings.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainFragment_to_settingsFragment
            )
        }
    }
}