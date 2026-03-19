package com.example.playlistmaker.presentation.search

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var inputSearchText: EditText
    private lateinit var btnClearSearch: ImageView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var errorState: LinearLayout
    private lateinit var btnRetry: View
    private lateinit var progressBar: View

    private lateinit var historyTitle: TextView
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var btnClearHistory: View

    private val tracksAdapter = TrackAdapter(mutableListOf())
    private val historyAdapter = TrackAdapter(mutableListOf())

    private var currentText: String = ""

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_search,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        inputSearchText = view.findViewById(R.id.inputSearchText)
        btnClearSearch = view.findViewById(R.id.btnClearSearch)
        tracksRecyclerView = view.findViewById(R.id.Tracks)
        emptyState = view.findViewById(R.id.emptyState)
        errorState = view.findViewById(R.id.errorState)
        btnRetry = view.findViewById(R.id.btnRetry)
        progressBar = view.findViewById(R.id.progressBar)

        historyTitle = view.findViewById(R.id.historyTitle)
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
        btnClearHistory = view.findViewById(R.id.btnClearHistory)

        tracksRecyclerView.adapter = tracksAdapter
        historyRecyclerView.adapter = historyAdapter

        tracksAdapter.onTrackClick = { track ->
            viewModel.onTrackClicked(track)
            openPlayer(track)
        }

        historyAdapter.onTrackClick = { track ->
            viewModel.onTrackClicked(track)
            openPlayer(track)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        inputSearchText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputSearchText.text.isNullOrEmpty()) {
                viewModel.onSearchFieldFocused()
            }
        }

        inputSearchText.doOnTextChanged { text, _, _, _ ->
            currentText = text.toString()
            viewModel.onSearchTextChanged(currentText)
        }

        inputSearchText.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {

                val imm = requireContext().getSystemService(InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(inputSearchText.windowToken, 0)

                true
            } else {
                false
            }
        }

        btnClearSearch.setOnClickListener {
            inputSearchText.text.clear()
        }

        btnRetry.setOnClickListener {
            viewModel.onRetry()
        }

        btnClearHistory.setOnClickListener {
            viewModel.onClearHistory()
        }
    }

    private fun render(state: SearchState) {

        progressBar.isVisible = state.isLoading
        errorState.isVisible = state.isError
        emptyState.isVisible = state.isEmpty

        tracksRecyclerView.isVisible = state.tracks.isNotEmpty()

        historyTitle.isVisible = state.showHistory
        historyRecyclerView.isVisible = state.showHistory
        btnClearHistory.isVisible = state.showHistory

        btnClearSearch.isVisible = state.showClearButton

        tracksAdapter.updateItems(state.tracks)
        historyAdapter.updateItems(state.history)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search_text", currentText)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val restoredText = savedInstanceState?.getString("search_text", "")
        inputSearchText.setText(restoredText)
    }

    private fun openPlayer(track: Track) {

        val bundle = bundleOf("track" to track)

        findNavController().navigate(
            R.id.action_searchFragment_to_audioPlayerFragment,
            bundle
        )
    }
}