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
import com.tech.imusic.R
import com.tech.imusic.model.Music
import com.tech.imusic.util.Utils

class FavoriteAdapter(val context: Context, private var musicArraylist: ArrayList<Music>) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.favorite_view_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = musicArraylist[position]

        holder.songNameFV?.text = model.title

        if(model.id == PlayerActivity.nowPlayingId){
            changeTextColor(holder)
        }else{
            removeTextColor(holder)
        }

        //music icon image
        Glide.with(context)
            .load(model.artUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.music_listener_image)
                    .centerCrop()
            )
            .into(holder.songImageFV!!)

        holder.itemView.setOnClickListener {

            if(musicArraylist[position].id == PlayerActivity.nowPlayingId){
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", PlayerActivity.songPosition)
                intent.putExtra("class", "NowPlaying")
                context.startActivity(intent)
            }else {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "FavoriteAdapter")
                context.startActivity(intent)
            }
        }
    }
    private fun removeTextColor(holder: ViewHolder) {
        holder.songNameFV?.setTextColor(ContextCompat.getColor(context,R.color.white))
        holder.songNameFV?.isSelected = false
    }
    private fun changeTextColor(holder: ViewHolder) {
        holder.songNameFV?.setTextColor(ContextCompat.getColor(context,R.color.cool_Pink))
        holder.songNameFV?.isSelected = true
    }

    override fun getItemCount(): Int {
        return musicArraylist.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songNameFV: TextView? = null
        var songImageFV: ImageView? = null

        init {
            songNameFV = itemView.findViewById(R.id.songNameFV)
            songImageFV = itemView.findViewById(R.id.songImgFV)
        }
    }

}