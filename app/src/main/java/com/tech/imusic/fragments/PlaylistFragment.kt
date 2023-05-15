package com.tech.imusic.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.tech.imusic.R
import com.tech.imusic.adapter.PlaylistAdapter
import com.tech.imusic.databinding.AddPlaylistDialogBinding
import com.tech.imusic.databinding.FragmentPlaylistBinding
import com.tech.imusic.model.MusicPlaylist
import com.tech.imusic.model.Playlist
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlaylistFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var adapter : PlaylistAdapter
    companion object{
        var musicPlaylist = MusicPlaylist()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentPlaylistBinding.inflate(layoutInflater, container, false)

        binding.recyclerPlaylist.layoutManager = GridLayoutManager(activity,2)

        adapter = PlaylistAdapter(requireActivity(), playlistList = musicPlaylist.ref)
        binding.recyclerPlaylist.adapter = adapter

        binding.recyclerPlaylist.setHasFixedSize(true)
        binding.recyclerPlaylist.setItemViewCacheSize(13)

        binding.playlistAddBtn.setOnClickListener {
            customAlertDialog()
        }

        return binding.root
    }

    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(activity).inflate(R.layout.add_playlist_dialog,binding.root,false)

        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setView(customDialog)
        builder.setTitle("Playlist Details")
            .setPositiveButton("ADD") { dialog, _ ->

              val playlistName = binder.playlistName.text
              val createdBy = binder.userName.text
                if(playlistName != null && createdBy != null){
                    if(playlistName.isNotEmpty() && createdBy.isNotEmpty()){
                        addPlaylist(playlistName.toString(),createdBy.toString())
                    }
                }
                dialog.dismiss()
            }.show()
    }

    private fun addPlaylist(playlistName: String, createdBy: String) {
        var playlistExists = false
        for (i in musicPlaylist.ref){
            if(playlistName == i.playListName){
                playlistExists = true
                break
            }
        }
        if(playlistExists){
            Toast.makeText(activity, "Playlist Exists!!", Toast.LENGTH_SHORT).show()
        }else{
            val tempPlaylist = Playlist()
            tempPlaylist.playListName = playlistName
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            val calender= Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calender)

            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

}