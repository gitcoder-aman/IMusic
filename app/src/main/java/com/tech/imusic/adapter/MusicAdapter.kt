package com.tech.imusic.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tech.imusic.PlayerActivity
import com.tech.imusic.PlaylistDetailActivity
import com.tech.imusic.R
import com.tech.imusic.fragments.PlaylistFragment
import com.tech.imusic.model.Music
import com.tech.imusic.util.Utils

class MusicAdapter(
    val context: Context,
    private var musicArraylist: ArrayList<Music>,
    private var playlistDetail: Boolean = false,
    private var searchingViewActivity: Boolean = false,
    private var playlistSelectionActivity: Boolean = false

) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_view_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicAdapter.ViewHolder, position: Int) {
        val model = musicArraylist[position]

        holder.songName?.text = model.title
        holder.songAlbumName?.text = model.album
        holder.songDuration?.text = Utils.formatDuration(model.duration)

        //music icon image
        Glide.with(context)
            .load(model.artUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.music_listener_image)
                    .centerCrop()
            )
            .into(holder.songImage!!)

        if (playlistDetail) {
            holder.itemView.setOnClickListener {
                sendIntent(position,"PlaylistDetailsAdapter")
            }
        } else {
            if (playlistSelectionActivity) {
                holder.itemView.setOnClickListener {
                    if(addSongInPlaylist((musicArraylist[position]))){
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.cool_Pink))
                    }else{
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.background))
                    }

                }
            }else if(searchingViewActivity){
                holder.itemView.setOnClickListener {
                    sendIntent(position,"MusicSearchViewActivity")
                }
            } else {
                holder.itemView.setOnClickListener {

                    if (musicArraylist[position].id == PlayerActivity.nowPlayingId) {
                        sendIntent(PlayerActivity.songPosition,"NowPlaying")
                    } else {
                        sendIntent(position,"MusicAdapter")
                    }
                }
            }
        }
    }

    private fun addSongInPlaylist(song: Music) : Boolean {
        PlaylistFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPos].playlist.forEachIndexed{index, music ->
            if(song.id == music.id){  //exists in Playlist then you can n't add this song
                PlaylistFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPos].playlist.add(song)
        return true
    }

    private fun sendIntent(position: Int, `class`: String) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", position)
        intent.putExtra("class", `class`)
        context.startActivity(intent)
    }
     fun refreshPlaylist(){
        musicArraylist = ArrayList()
        musicArraylist = PlaylistFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return musicArraylist.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songName: TextView? = null
        var songAlbumName: TextView? = null
        var songImage: ImageView? = null
        var songDuration: TextView? = null

        init {
            songName = itemView.findViewById(R.id.song_name)
            songAlbumName = itemView.findViewById(R.id.song_album)
            songDuration = itemView.findViewById(R.id.song_duration)
            songImage = itemView.findViewById(R.id.imageMV)
        }
    }

}