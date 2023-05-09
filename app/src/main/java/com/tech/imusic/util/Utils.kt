package com.tech.imusic.util

import android.media.MediaMetadataRetriever
import com.tech.imusic.PlayerActivity
import com.tech.imusic.fragments.FavoriteFragment
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
        fun checkMusicListExitORNotInFILE(playlist:ArrayList<Music>) : ArrayList<Music>{
            playlist.forEachIndexed{index, music ->
                val file = File(music.path)
                if(!file.exists()){
                    playlist.removeAt(index)
                }
            }
            return playlist
        }
    }
}