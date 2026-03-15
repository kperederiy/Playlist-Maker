package com.example.playlistmaker.presentation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_library,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val adapter = LibraryViewPagerAdapter(
            childFragmentManager,
            lifecycle
        )

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->

            when (position) {
                0 -> tab.text = getString(R.string.tab_favourite_tracks)
                1 -> tab.text = getString(R.string.tab_playlists)
            }

        }.attach()
    }

    companion object {
        fun newInstance() = LibraryFragment()
    }
}