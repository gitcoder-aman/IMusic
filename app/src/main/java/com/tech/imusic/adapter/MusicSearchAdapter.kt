package com.tech.imusic.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tech.imusic.PlayerActivity
import com.tech.imusic.R
import com.tech.imusic.model.Music
import com.tech.imusic.util.Utils

class MusicSearchAdapter(val context: Context, var musicArraylist: ArrayList<Music>) :
    RecyclerView.Adapter<MusicSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicSearchAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_view_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicSearchAdapter.ViewHolder, position: Int) {
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

        holder.itemView.setOnClickListener {
            if(musicArraylist[position].id == PlayerActivity.nowPlayingId){
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", PlayerActivity.songPosition)
                intent.putExtra("class", "NowPlaying")
                context.startActivity(intent)
            }else{
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index",position)
                intent.putExtra("class","MusicSearchAdapter")
                context.startActivity(intent)
            }
        }
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