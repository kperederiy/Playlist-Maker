package com.example.playlistmaker.presentation.playlists

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.color.MaterialColors

class PlaylistFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private var playlistId: Long = 0L

    private lateinit var bottomSheetBehavior:
            BottomSheetBehavior<LinearLayout>

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
        initShareButton()
        initBottomSheet()
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

    private fun initShareButton() {

        binding.shareButton.setOnClickListener {

            processShareClick()
        }
    }

    private fun processShareClick() {

        val tracks = viewModel.tracks.value

        if (tracks.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "В этом плейлисте нет списка треков, которым можно поделиться",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            sharePlaylist(tracks)
        }
    }

    private fun sharePlaylist(tracks: List<Track>) {

        val playlist = viewModel.playlist.value
            ?: return

        val shareText = buildShareText(
            playlist.name,
            playlist.description,
            tracks
        )

        val intent = Intent(Intent.ACTION_SEND)

        intent.type = "text/plain"

        intent.putExtra(
            Intent.EXTRA_TEXT,
            shareText
        )

        startActivity(
            Intent.createChooser(intent, null)
        )
    }

    private fun buildShareText(
        playlistName: String,
        playlistDescription: String,
        tracks: List<Track>
    ): String {

        val builder = StringBuilder()

        builder.appendLine(playlistName)

        if (playlistDescription.isNotEmpty()) {

            builder.appendLine(playlistDescription)
        }

        builder.appendLine(
            resources.getQuantityString(
                R.plurals.tracks_count,
                tracks.size,
                tracks.size
            )
        )

        tracks.forEachIndexed { index, track ->

            builder.appendLine(
                "${index + 1}. " +
                        "${track.artistName} - " +
                        "${track.trackName} " +
                        "(${track.trackTime})"
            )
        }

        return builder.toString()
    }

    private fun initBottomSheet() {

        val bottomSheet =
            binding.menuBottomSheet

        val overlay =
            binding.overlay

        bottomSheetBehavior =
            BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.isHideable = true

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(
                    bottomSheet: View,
                    newState: Int
                ) {

                    overlay.visibility =
                        if (newState == BottomSheetBehavior.STATE_HIDDEN)
                            View.GONE
                        else
                            View.VISIBLE
                }

                override fun onSlide(
                    bottomSheet: View,
                    slideOffset: Float
                ) {}
            }
        )

        binding.menuButton.setOnClickListener {

            fillBottomSheet()

            bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_COLLAPSED
        }

        overlay.setOnClickListener {

            bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN
        }

        binding.shareMenuItem.setOnClickListener {

            bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN

            processShareClick()
        }

        binding.editMenuItem.setOnClickListener {

            bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN

            val playlist =
                viewModel.playlist.value
                    ?: return@setOnClickListener

            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                Bundle().apply {
                    putParcelable(
                        "playlist",
                        playlist
                    )
                }
            )
        }

        binding.deleteMenuItem.setOnClickListener {

            bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN

            showDeletePlaylistDialog()
        }
    }

    private fun fillBottomSheet() {

        val playlist =
            viewModel.playlist.value
                ?: return

        binding.bottomSheetPlaylistName.text =
            playlist.name

        binding.bottomSheetTracksCount.text =
            resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.tracksCount,
                playlist.tracksCount
            )

        if (playlist.coverPath.isNotEmpty()) {

            Glide.with(requireContext())
                .load(playlist.coverPath)
                .placeholder(R.drawable.placeholder)
                .into(binding.bottomSheetCover)

        } else {

            binding.bottomSheetCover
                .setImageResource(R.drawable.placeholder)
        }
    }

    private fun showDeletePlaylistDialog() {

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Хотите удалить плейлист?")

            .setNegativeButton("Нет") { dialog, _ ->

                dialog.dismiss()
            }

            .setPositiveButton("Да") { dialog, _ ->

                dialog.dismiss()

                deletePlaylist()
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

    private fun deletePlaylist() {

        viewModel.deletePlaylist()

        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val PLAYLIST_ID_KEY = "playlist_id"
    }
}