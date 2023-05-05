package com.tech.imusic.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tech.imusic.PlayerActivity
import com.tech.imusic.R
import com.tech.imusic.application.ApplicationClass
import com.tech.imusic.fragments.NowPlayingFragment
import com.tech.imusic.util.Utils
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        //all functionality on notification click listener
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> {
                prevNextSong(false,context!!)
                PlayerActivity.binding.lottieAnimationView.playAnimation()
            }

            ApplicationClass.PLAY -> {
                if (PlayerActivity.isPlaying) {
                    pauseMusic()
                } else {
                    playMusic()
                }
            }

            ApplicationClass.NEXT -> {
                prevNextSong(true,context!!)
                PlayerActivity.binding.lottieAnimationView.playAnimation()
            }

            ApplicationClass.EXIT -> {
                Utils.exitApplication()
            }
        }
    }

    private fun playMusic() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause_notification)
        PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
        NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_pause)
        PlayerActivity.binding.lottieAnimationView.playAnimation()
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_play_notification)
        PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_play)
        NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_play)
        PlayerActivity.binding.lottieAnimationView.pauseAnimation()
    }

    private fun prevNextSong(increment: Boolean, context: Context) {

        Utils.setSongPosition(increment)

        PlayerActivity.musicService?.createMediaPlayer()

        Glide.with(context)
            .load(PlayerActivity.musicArrayList[PlayerActivity.songPosition].artUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.music_listener_image)
                    .centerCrop()
            )
            .into(PlayerActivity.binding.songImageView)

        Glide.with(context)
            .load(PlayerActivity.musicArrayList[PlayerActivity.songPosition].artUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.music_listener_image)
                    .centerCrop()
            )
            .into(NowPlayingFragment.binding.songImgFrag)

        NowPlayingFragment.binding.songNameFrag.text = PlayerActivity.musicArrayList[PlayerActivity.songPosition].title
        PlayerActivity.binding.songName.text = PlayerActivity.musicArrayList[PlayerActivity.songPosition].title

        playMusic()
    }
}