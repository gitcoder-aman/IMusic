package com.tech.imusic.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tech.imusic.PlayerActivity
import com.tech.imusic.PlaylistDetailActivity
import com.tech.imusic.R
import com.tech.imusic.databinding.PlaylistViewLayoutBinding
import com.tech.imusic.fragments.PlaylistFragment
import com.tech.imusic.model.Music
import com.tech.imusic.model.Playlist
import com.tech.imusic.util.Utils

class PlaylistAdapter(val context: Context, private var playlistList: ArrayList<Playlist>) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistAdapter.ViewHolder {

        return ViewHolder(PlaylistViewLayoutBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: PlaylistAdapter.ViewHolder, position: Int) {
        val model = playlistList[position]

        holder.playlistName?.isSelected = true
        holder.playlistName?.text = model.playListName

        if(PlaylistFragment.musicPlaylist.ref[position].playlist.size > 0){
            //music icon image
            Glide.with(context)
                .load(model.playlist[0].artUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.music_listener_image)
                        .centerCrop()
                )
                .into(holder.playlistSongImg!!)
        }

        holder.playlistDeleteBtn?.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].playListName)
                .setMessage("Do you want to delete this Playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlaylistFragment.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
        }
        holder.root.setOnClickListener {
            val intent = Intent(context,PlaylistDetailActivity::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }
    fun refreshPlaylist(){
        playlistList = ArrayList()
        playlistList.addAll(PlaylistFragment.musicPlaylist.ref)
        notifyDataSetChanged()
    }

    class ViewHolder(binding: PlaylistViewLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        var playlistSongImg: ImageView? = binding.playListSongImage
        var playlistName: TextView? = binding.playListName
        var playlistDeleteBtn: ImageButton? = binding.playListDeleteBtn
        val root = binding.root

    }

}