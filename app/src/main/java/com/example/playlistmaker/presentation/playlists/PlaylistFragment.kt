package com.example.playlistmaker.presentation.playlists

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.google.android.material.color.MaterialColors

class PlaylistFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private var playlistId: Long = 0L

    private val tracksAdapter =
        TrackAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playlistId = requireArguments().getLong(PLAYLIST_ID_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaylistBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        initBackPressed()
        initRecycler()

        viewModel.loadPlaylist(playlistId)

        observeViewModel()
        observeDuration()
        observeTracks()
    }

    private fun initToolbar() {

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initBackPressed() {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner
        ) {

            findNavController().navigateUp()
        }
    }

    private fun initRecycler() {

        tracksAdapter.onTrackClick = { track ->

            findNavController().navigate(
                R.id.action_playlistFragment_to_audioPlayerFragment,
                Bundle().apply {
                    putParcelable("track", track)
                }
            )
        }

        tracksAdapter.onTrackLongClick = { track ->

            showDeleteDialog(track)
        }

        binding.tracksRecyclerView.apply {

            layoutManager = LinearLayoutManager(requireContext())

            adapter = tracksAdapter
        }
    }

    private fun showDeleteDialog(track: Track) {

        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("Хотите удалить трек?")

            .setNegativeButton("НЕТ") { dialog, _ ->

                dialog.dismiss()
            }

            .setPositiveButton("ДА") { dialog, _ ->

                dialog.dismiss()

                viewModel.removeTrack(track)
            }

            .show()

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(
                    MaterialColors.getColor(
                        requireView(),
                        com.google.android.material.R.attr.colorOnSecondary
                    )
                )

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(
                    MaterialColors.getColor(
                        requireView(),
                        com.google.android.material.R.attr.colorOnSecondary
                    )
                )
    }

    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.playlist.collect { playlist ->

                playlist ?: return@collect

                binding.playlistName.text = playlist.name

                if (playlist.description.isEmpty()) {

                    binding.playlistDescription.visibility = View.GONE

                } else {

                    binding.playlistDescription.visibility = View.VISIBLE
                    binding.playlistDescription.text = playlist.description
                }

                binding.tracksCount.text =
                    "${playlist.tracksCount} треков"

                if (playlist.coverPath.isNotEmpty()) {

                    Glide.with(requireContext())
                        .load(playlist.coverPath)
                        .placeholder(R.drawable.placeholder)
                        .into(binding.cover)

                } else {

                    binding.cover.setImageResource(R.drawable.placeholder)
                }

                binding.tracksCount.text =
                    resources.getQuantityString(
                        R.plurals.tracks_count,
                        playlist.tracksCount,
                        playlist.tracksCount
                    )
            }
        }
    }

    private fun observeDuration() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.duration.collect { duration ->

                val minutes = duration.toIntOrNull() ?: 0

                binding.duration.text =
                    resources.getQuantityString(
                        R.plurals.playlist_duration_minutes,
                        minutes,
                        minutes
                    )
            }
        }
    }

    private fun observeTracks() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.tracks.collect { tracks ->

                tracksAdapter.updateItems(tracks)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val PLAYLIST_ID_KEY = "playlist_id"
    }
}