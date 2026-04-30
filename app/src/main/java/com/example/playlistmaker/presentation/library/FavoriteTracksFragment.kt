package com.example.playlistmaker.presentation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private val viewModel: FavoriteTracksViewModel by viewModel()

    private val adapter = TrackAdapter(mutableListOf())

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter.onTrackClick = { track ->
            onTrackClick(track)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {

                is FavoriteTracksState.Empty -> {
                    binding.libraryEmpty.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }

                is FavoriteTracksState.Content -> {
                    binding.libraryEmpty.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE

                    adapter.updateItems(state.tracks)
                }
            }
        }
    }

    private fun onTrackClick(track: Track) {
        val bundle = Bundle().apply {
            putSerializable("track", track)
        }

        findNavController().navigate(
            R.id.action_libraryFragment_to_audioPlayerFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}