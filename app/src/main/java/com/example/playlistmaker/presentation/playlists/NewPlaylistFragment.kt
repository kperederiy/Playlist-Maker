package com.example.playlistmaker.presentation.playlists

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import java.io.File
import java.io.FileOutputStream
import androidx.activity.addCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private var coverUri: Uri? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                coverUri = uri
                binding.cover.setImageURI(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        initBackPressed()
        initCoverPicker()
        initCreateButton()
        initNameInput()
    }

    private fun initBackPressed() {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner
        ) {

            handleBackAction()
        }
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            handleBackAction()
        }
    }

    private fun handleBackAction() {

        if (hasUnsavedChanges()) {

            showExitDialog()

        } else {

            findNavController().navigateUp()
        }
    }

    private fun hasUnsavedChanges(): Boolean {

        return coverUri != null ||
                binding.nameEditText.text?.isNotEmpty() == true ||
                binding.descriptionEditText.text?.isNotEmpty() == true
    }

    private fun showExitDialog() {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")

            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }

            .setPositiveButton("Завершить") { _, _ ->
                findNavController().navigateUp()
            }

            .show()
    }

    private fun initCoverPicker() {
        binding.coverContainer.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    private fun initNameInput() {
        binding.nameEditText.doOnTextChanged { text, _, _, _ ->
            binding.createButton.isEnabled = !text.isNullOrEmpty()
        }
    }

    private fun initCreateButton() {

        binding.createButton.setOnClickListener {

            val playlistName =
                binding.nameEditText.text.toString().trim()

            if (playlistName.isEmpty()) return@setOnClickListener

            val description =
                binding.descriptionEditText.text.toString()

            val imagePath = coverUri?.let {
                saveImageToPrivateStorage(it)
            } ?: ""

            viewModel.createPlaylist(
                name = playlistName,
                description = description,
                coverPath = imagePath
            )

            Toast.makeText(
                requireContext(),
                "Плейлист $playlistName создан",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().navigateUp()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {

        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_covers"
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(
            filePath,
            "cover_${System.currentTimeMillis()}.jpg"
        )

        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return file.absolutePath
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}