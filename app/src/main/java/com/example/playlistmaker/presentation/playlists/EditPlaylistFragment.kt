package com.example.playlistmaker.presentation.playlists

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.domain.model.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment :
    NewPlaylistFragment() {

    override val viewModel:
            EditPlaylistViewModel by viewModel()

    private var playlist: Playlist? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        playlist =
            arguments?.getParcelable("playlist")
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        setupUi()

        playlist?.let {

            viewModel.setPlaylist(it)
        }
    }

    private fun setupUi() {

        binding.toolbar.title =
            "Редактировать"

        binding.createButton.text =
            "Сохранить"

        val currentPlaylist =
            playlist ?: return

        binding.nameEditText.setText(
            currentPlaylist.name
        )

        binding.descriptionEditText.setText(
            currentPlaylist.description
        )

        if (currentPlaylist.coverPath.isNotEmpty()) {

            binding.cover.setImageURI(
                Uri.parse(currentPlaylist.coverPath)
            )
        }
    }

    override fun handleBackAction() {

        findNavController().navigateUp()
    }

    override fun createPlaylist() {

        val playlistName =
            binding.nameEditText.text.toString()

        if (playlistName.isEmpty()) return

        val description =
            binding.descriptionEditText.text.toString()

        val imagePath =
            coverUri?.let {
                saveImageToPrivateStorage(it)
            } ?: playlist?.coverPath.orEmpty()

        viewModel.updatePlaylist(
            name = playlistName,
            description = description,
            coverPath = imagePath
        )

        findNavController().navigateUp()
    }
}