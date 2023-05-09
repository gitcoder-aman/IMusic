package com.tech.imusic.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tech.imusic.PlayerActivity
import com.tech.imusic.R
import com.tech.imusic.adapter.FavoriteAdapter
import com.tech.imusic.adapter.MusicAdapter
import com.tech.imusic.databinding.FragmentFavoriteBinding
import com.tech.imusic.model.Music
import com.tech.imusic.util.Utils

class FavoriteFragment : Fragment() {

    companion object{
         var favoriteList:ArrayList<Music> = ArrayList()
         @SuppressLint("StaticFieldLeak")
         var adapter: FavoriteAdapter ?= null
        var binding:FragmentFavoriteBinding ?= null

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentFavoriteBinding.inflate(layoutInflater, container, false)

        favoriteList = Utils.checkMusicListExitORNotInFILE(favoriteList)
        binding!!.recyclerFavorite.layoutManager = GridLayoutManager(activity,4)

        adapter = FavoriteAdapter(requireActivity(), favoriteList)
        binding!!.recyclerFavorite.adapter = adapter

        binding!!.recyclerFavorite.setHasFixedSize(true)
        binding!!.recyclerFavorite.setItemViewCacheSize(13)

        binding!!.favoriteShuffle.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","FavoriteFragmentShuffle")
            context?.startActivity(intent)
        }
        return binding!!.root
    }

}