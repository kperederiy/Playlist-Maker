package com.example.playlistmaker.presentation.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.presentation.playlists.adapter.PlaylistsAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val adapter = PlaylistsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPlaylistsBinding.inflate(
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

        initRecycler()

        binding.newPlaylist.setOnClickListener {

            findNavController().navigate(
                R.id.action_libraryFragment_to_newPlaylistFragment
            )
        }

        observeViewModel()
    }

    private fun initRecycler() {

        binding.playlistsRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)

        binding.playlistsRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.getPlaylists().collect { playlists ->

                adapter.submitList(playlists)

                if (playlists.isEmpty()) {

                    binding.playlistsEmpty.visibility = View.VISIBLE
                    binding.playlistsRecyclerView.visibility = View.GONE

                } else {

                    binding.playlistsEmpty.visibility = View.GONE
                    binding.playlistsRecyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}