package com.tech.imusic

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.tech.imusic.adapter.MusicAdapter
import com.tech.imusic.databinding.ActivityPlaylistDetailBinding
import com.tech.imusic.fragments.FavoriteFragment
import com.tech.imusic.fragments.PlaylistFragment
import com.tech.imusic.model.Music
import com.tech.imusic.model.MusicPlaylist
import com.tech.imusic.util.Utils

class PlaylistDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailBinding
    private lateinit var adapter: MusicAdapter

    companion object {
        var currentPlaylistPos: Int = -1
        var playlistList: ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPlaylistPos = intent.extras?.getInt("index")!!
        PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist = Utils.checkMusicListExitORNotInFILE(playlist = PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist)

        binding.recyclerPlaylistPD.setItemViewCacheSize(10)
        binding.recyclerPlaylistPD.setHasFixedSize(true)
        binding.recyclerPlaylistPD.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapter = MusicAdapter(
            this,
            PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist,
            playlistDetail = true
        )
        binding.recyclerPlaylistPD.adapter = adapter

        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.playlistShuffler.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistShuffle")
            startActivity(intent)
        }
        binding.addBtnPD.setOnClickListener {
            val intent = Intent(this, PlaylistSelectionActivity::class.java)
            startActivity(intent)
        }
        binding.removeBtnPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
            builder.setMessage("Do You want to remove all songs from playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text =
            PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playListName
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Song.\n\n" +
                "Created On: \n${PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n" +
                "Created By: \n${PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].createdBy}"

        if (adapter.itemCount > 0) {
            Glide.with(this)
                .load(PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.music_listener_image)
                        .centerCrop()
                )
                .into(binding.playlistImagePD)
            binding.playlistShuffler.visibility = View.VISIBLE
        }
        //update the playlist after add the music
        adapter.notifyDataSetChanged()
        //for shuffle
        playlistList.addAll(PlaylistFragment.musicPlaylist.ref[currentPlaylistPos].playlist) //all the add playlist after selecting from playlist detail

        //all save playlist data after close at Playlist detail activity

        val editor = getSharedPreferences("FAVORITES_PLAYLIST", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistFragment.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
    }
}