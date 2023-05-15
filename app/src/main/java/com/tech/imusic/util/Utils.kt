package com.tech.imusic.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import com.tech.imusic.PlayerActivity
import com.tech.imusic.R
import com.tech.imusic.adapter.MusicAdapter
import com.tech.imusic.fragments.FavoriteFragment
import com.tech.imusic.fragments.PlaylistFragment
import com.tech.imusic.model.Music
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class Utils {
    companion object{
        fun formatDuration(duration:Long):String{
            val minutes = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
            val seconds = (TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)
                    - minutes * TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))

            return String.format("%02d:%02d",minutes,seconds)
        }
        fun getImgArt(path:String): ByteArray? {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(path)
            return retriever.embeddedPicture
        }

         fun setSongPosition(increment: Boolean) {
            if(!PlayerActivity.repeatMusic){
                if (increment) {
                    if (PlayerActivity.musicArrayList.size - 1 == PlayerActivity.songPosition) {
                        PlayerActivity.songPosition = 0
                    } else {
                        ++PlayerActivity.songPosition
                    }
                } else {
                    if (PlayerActivity.songPosition == 0) {
                        PlayerActivity.songPosition = PlayerActivity.musicArrayList.size - 1
                    } else {
                        --PlayerActivity.songPosition
                    }
                }
            }
        }
        fun exitApplication(){
            if(PlayerActivity.musicService != null) {
                PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
                PlayerActivity.musicService!!.stopForeground(true)
                PlayerActivity.musicService!!.mediaPlayer!!.release()
                PlayerActivity.musicService = null
                exitProcess(1)
            }
        }
        fun favoriteChecker(id:String):Int{
            PlayerActivity.isFavorite = false
            FavoriteFragment.favoriteList.forEachIndexed{index, music ->
                if(id == music.id){
                    PlayerActivity.isFavorite = true
                    return index
                }
            }
            return -1
        }
        fun sharedPrefPlaylist(context: Context){
            val editor = context.getSharedPreferences("FAVORITES_PLAYLIST", AppCompatActivity.MODE_PRIVATE).edit()
            val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistFragment.musicPlaylist)
            editor.putString("MusicPlaylist", jsonStringPlaylist)
            editor.apply()
        }
        fun checkMusicListExitORNotInFILE(playlist:ArrayList<Music>) : ArrayList<Music>{
            playlist.forEachIndexed{index, music ->
                val file = File(music.path)
                if(!file.exists()){
                    playlist.removeAt(index)
                }
            }
            return playlist
        }
        @SuppressLint("QueryPermissionsNeeded")
        fun instagramOpen(activity: Activity) {
            val username = activity.resources.getString(R.string.insta_id)
            val uri = Uri.parse("http://instagram.com/_u/$username")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.instagram.android")

// Check if Instagram app is installed
            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(intent)
            } else {
                // Instagram app is not installed, open in browser
                val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                activity.startActivity(browserIntent)
            }
        }
        @SuppressLint("QueryPermissionsNeeded")
        fun linkedinOpen(activity: Activity){
            val profileId = activity.resources.getString(R.string.linkedin_id)
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse("linkedin://profile/$profileId")
            intent.data = uri
            intent.setPackage("com.linkedin.android")

// Check if LinkedIn app is installed
            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(intent)
            } else {
                // LinkedIn app is not installed, open in browser
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/profile/view?id=$profileId"))
                activity.startActivity(browserIntent)
            }
        }
    }
}