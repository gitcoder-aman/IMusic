package com.tech.imusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.tech.imusic.adapter.MusicAdapter
import com.tech.imusic.databinding.ActivityPlaylistSelectionBinding
import com.tech.imusic.fragments.SongFragment
import com.tech.imusic.model.Music

class PlaylistSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistSelectionBinding
    private lateinit var musicSelectionSearchAdapter: MusicAdapter
    var filteredMusic = ArrayList<Music>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerSelection.setItemViewCacheSize(10)
        binding.recyclerSelection.setHasFixedSize(true)
        binding.recyclerSelection.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )

        musicSelectionSearchAdapter = MusicAdapter(this,SongFragment.musicArrayList,
            playlistDetail = false,
            searchingViewActivity = false,
            playlistSelectionActivity = true)

        binding.recyclerSelection.adapter = musicSelectionSearchAdapter

        binding.selectionBackBtn.setOnClickListener {
            finish()
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val searchText = newText.lowercase()
                    filteredMusic = musicFiltering(searchText)

                    musicSelectionSearchAdapter = MusicAdapter(
                        this@PlaylistSelectionActivity,
                        filteredMusic, playlistDetail = false,searchingViewActivity = false, playlistSelectionActivity = true)
                    binding.recyclerSelection.adapter = musicSelectionSearchAdapter
                }
                return true
            }

        })
    }
    private fun musicFiltering(searchText: String) : ArrayList<Music> {
        val newFilteredList = arrayListOf<Music>()
        for (song in SongFragment.musicArrayList) {
            if (song.album.lowercase().contains(searchText) || song.artist.lowercase()
                    .contains(searchText) || song.title.lowercase().contains(searchText)
            ) {
                newFilteredList.add(song)
            }
        }
        if(newFilteredList.size < 1){
            binding.noSearchFound.visibility = View.VISIBLE
            binding.recyclerSelection.visibility = View.INVISIBLE
        }else{
            binding.noSearchFound.visibility = View.INVISIBLE
            binding.recyclerSelection.visibility = View.VISIBLE
        }
        return newFilteredList
    }
}