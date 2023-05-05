package com.tech.imusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.tech.imusic.adapter.MusicAdapter
import com.tech.imusic.adapter.MusicSearchAdapter
import com.tech.imusic.databinding.ActivitySearchViewBinding
import com.tech.imusic.fragments.SongFragment
import com.tech.imusic.model.Music
import java.util.Locale

class SearchViewActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySearchViewBinding
    private  var musicSearchAdapter: MusicSearchAdapter ?= null

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
                musicSearchAdapter = MusicSearchAdapter(this@SearchViewActivity,filteredMusic)
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
//            musicSearchAdapter?.filteringForSearch(newFilteredList)
        }
        return newFilteredList
    }
}