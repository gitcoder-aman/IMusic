package com.tech.imusic.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tech.imusic.fragments.FavoriteFragment
import com.tech.imusic.fragments.PlaylistFragment
import com.tech.imusic.fragments.SongFragment

open class FragmentsAdapter(private val fm : FragmentManager, private val lifecycle: Lifecycle) : FragmentStateAdapter(fm,lifecycle)
{
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return SongFragment()
            }
            1 -> {
                return FavoriteFragment()
            }
            2 -> {
                return PlaylistFragment()
            }

        }
        return SongFragment()
    }
}