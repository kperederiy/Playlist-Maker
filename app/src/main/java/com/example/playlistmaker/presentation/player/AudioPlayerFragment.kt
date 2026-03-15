package com.example.playlistmaker.presentation.player

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.google.android.material.appbar.MaterialToolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private val viewModel: AudioPlayerViewModel by viewModel()

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

        val rootView = view.findViewById<View>(R.id.root)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }

        val track = requireArguments().getSerializable(TRACK_KEY) as Track

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

        val btnPlaylist = view.findViewById<ImageView>(R.id.btnPlaylist)

        var isInPlaylist = false
        btnPlaylist.setOnClickListener {

            isInPlaylist = !isInPlaylist

            if (isInPlaylist) {
                btnPlaylist.setImageResource(R.drawable.ic_del_playlist_51)
            } else {
                btnPlaylist.setImageResource(R.drawable.ic_add_playlist_51)
            }
        }

        val btnLike = view.findViewById<ImageView>(R.id.btnLike)

        var isLiked = false
        btnLike.setOnClickListener {

            isLiked = !isLiked

            if (isLiked) {
                btnLike.setImageResource(R.drawable.ic_del_favorite_51)
            } else {
                btnLike.setImageResource(R.drawable.ic_add_favorite_51)
            }
        }

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {

        private const val TRACK_KEY = "track"

        fun newInstance(track: Track): AudioPlayerFragment {
            return AudioPlayerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TRACK_KEY, track)
                }
            }
        }
    }
}
fun Track.getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")