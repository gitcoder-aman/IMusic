package com.tech.imusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tech.imusic.adapter.MusicAdapter
import com.tech.imusic.databinding.ActivitySearchViewBinding
import com.tech.imusic.fragments.SongFragment
import com.tech.imusic.model.Music

class SearchViewActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySearchViewBinding
    private  var musicSearchAdapter: MusicAdapter ?= null

    companion object{
        var filteredMusic = ArrayList<Music>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBackBtn.setOnClickListener {
            finish()
        }
        binding.searchRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        binding.searchEdittext.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)=Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = p0.toString().trim()
                filteredMusic = musicFiltering(searchText)
                musicSearchAdapter = MusicAdapter(this@SearchViewActivity,filteredMusic,false,
                    searchingViewActivity = true,
                    playlistSelectionActivity = false)
                binding.searchRecycler.adapter = musicSearchAdapter
            }

            override fun afterTextChanged(p0: Editable?)= Unit

        })
    }

    private fun musicFiltering(searchText: String) : ArrayList<Music> {
        val newFilteredList = arrayListOf<Music>()
        for (song in SongFragment.musicArrayList){
            if(song.album.lowercase().contains(searchText) || song.artist.lowercase().contains(searchText) || song.title.lowercase().contains(searchText)){
                newFilteredList.add(song)
            }
        }
        if(newFilteredList.size < 1){
            binding.noSearchFound.visibility = View.VISIBLE
            binding.searchRecycler.visibility = View.INVISIBLE
        }else{
            binding.noSearchFound.visibility = View.INVISIBLE
            binding.searchRecycler.visibility = View.VISIBLE
        }
        return newFilteredList
    }
}