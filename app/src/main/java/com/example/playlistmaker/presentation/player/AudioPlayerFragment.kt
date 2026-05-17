package com.example.playlistmaker.presentation.player

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private val viewModel: AudioPlayerViewModel by viewModel()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_audio_player,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val track = arguments?.getParcelable<Track>("track") ?: return

        val cover = view.findViewById<ImageView>(R.id.cover)
        val trackName = view.findViewById<TextView>(R.id.trackName)
        val trackTime = view.findViewById<TextView>(R.id.trackTime)
        val artistName = view.findViewById<TextView>(R.id.artistName)
        val collection = view.findViewById<TextView>(R.id.collectionName)
        val genre = view.findViewById<TextView>(R.id.primaryGenreName)
        val country = view.findViewById<TextView>(R.id.country)
        val releaseDate = view.findViewById<TextView>(R.id.releaseDate)
        val btnPlay = view.findViewById<ImageView>(R.id.btnPlay)
        val durationTextView = view.findViewById<TextView>(R.id.duration)

        Glide.with(requireContext())
            .load(track.getCoverArtwork())
            .into(cover)

        trackName.text = track.trackName
        trackTime.text = track.trackTime
        artistName.text = track.artistName
        collection.text = track.collectionName
        genre.text = track.primaryGenreName
        country.text = track.country
        releaseDate.text = track.releaseDate.take(4)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            btnPlay.isEnabled = state.isPlayButtonEnabled
            durationTextView.text = state.currentTime

            btnPlay.setImageResource(
                if (state.isPlaying)
                    R.drawable.ic_pause_100
                else
                    R.drawable.ic_play_100
            )
        }

        btnPlay.setOnClickListener {
            viewModel.onPlayClicked()
        }

        viewModel.prepare(track.previewUrl)

        val btnLike = view.findViewById<ImageView>(R.id.btnLike)
        viewModel.setTrack(track)
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            btnLike.setImageResource(
                if (isFavorite)
                    R.drawable.ic_del_favorite_51
                else
                    R.drawable.ic_add_favorite_51
            )
        }
        btnLike.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }


        val bottomSheet =
            view.findViewById<LinearLayout>(R.id.playlists_bottom_sheet)

        val overlay =
            view.findViewById<View>(R.id.overlay)

        bottomSheetBehavior =
            BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_HIDDEN
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
        val playlistsAdapter = PlaylistBottomSheetAdapter()

        val recycler =
            view.findViewById<RecyclerView>(R.id.playlistsRecyclerView)

        recycler.adapter = playlistsAdapter

        recycler.layoutManager =
            LinearLayoutManager(requireContext())

//        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
//
//            playlistsAdapter.updateItems(playlists)
//        }

        val btnPlaylist = view.findViewById<ImageView>(R.id.btnPlaylist)
//        var isInPlaylist = false
//        btnPlaylist.setOnClickListener {
//
//            isInPlaylist = !isInPlaylist
//
//            if (isInPlaylist) {
//                btnPlaylist.setImageResource(R.drawable.ic_del_playlist_51)
//            } else {
//                btnPlaylist.setImageResource(R.drawable.ic_add_playlist_51)
//            }
//        }
        btnPlaylist.setOnClickListener {

            bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_COLLAPSED
        }

        val newPlaylist =
            view.findViewById<MaterialButton>(R.id.newPlaylist)

        newPlaylist.setOnClickListener {

            bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_HIDDEN

            findNavController().navigate(
                R.id.action_audioPlayerFragment_to_newPlaylistFragment
            )
        }
        viewLifecycleOwner.lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.playlists.collect { playlists ->

                    playlistsAdapter.submitList(playlists)
                }
            }
        }
    }
}
fun Track.getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")