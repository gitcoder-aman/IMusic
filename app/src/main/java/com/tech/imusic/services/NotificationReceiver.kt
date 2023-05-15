package com.tech.imusic.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tech.imusic.PlayerActivity
import com.tech.imusic.R
import com.tech.imusic.application.ApplicationClass
import com.tech.imusic.fragments.NowPlayingFragment
import com.tech.imusic.util.Utils

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        //for music control through bluetooth headset
        val action = intent!!.action
        if (Intent.ACTION_MEDIA_BUTTON == action) {
            val event: KeyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)!!
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (event.keyCode) {
                    KeyEvent.KEYCODE_MEDIA_PLAY -> {
                        sendCommandToService(context!!, "PLAY");
                    }
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                        sendCommandToService(context!!, "PAUSE");
                    }
                    KeyEvent.KEYCODE_MEDIA_NEXT -> {
                        sendCommandToService(context!!, "NEXT");
                    }
                    KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                        sendCommandToService(context!!, "PREVIOUS");
                    }
                }
            }
        }

        //all functionality on notification click listener
        when (action) {
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

    private fun sendCommandToService(context: Context, command: String) {
        // Helper method to send command to media player service.(bluetooth headset)
        val intent = Intent(context, MusicService::class.java)
        intent.action = command
        context.startService(intent)
    }

    companion object {
         fun playMusic() {
            PlayerActivity.isPlaying = true
            PlayerActivity.musicService!!.mediaPlayer!!.start()
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause_notification)
            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
            NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_pause)
            PlayerActivity.binding.lottieAnimationView.playAnimation()
        }

         fun pauseMusic() {
            PlayerActivity.isPlaying = false
            PlayerActivity.musicService!!.mediaPlayer!!.pause()
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_play_notification)
            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_play)
            NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_play)
            PlayerActivity.binding.lottieAnimationView.pauseAnimation()
        }

         fun prevNextSong(increment: Boolean, context: Context) {

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

            //song small fragment update
            Glide.with(context)
                .load(PlayerActivity.musicArrayList[PlayerActivity.songPosition].artUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.music_listener_image)
                        .centerCrop()
                )
                .into(NowPlayingFragment.binding.songImgFrag)

            NowPlayingFragment.binding.songNameFrag.text =
                PlayerActivity.musicArrayList[PlayerActivity.songPosition].title
            PlayerActivity.binding.songName.text =
                PlayerActivity.musicArrayList[PlayerActivity.songPosition].title

            playMusic()
            PlayerActivity.fIndex =
                Utils.favoriteChecker(PlayerActivity.musicArrayList[PlayerActivity.songPosition].id)
            if (PlayerActivity.isFavorite) {
                PlayerActivity.binding.favoriteBtn.setImageResource(R.drawable.ic_favorite)
            } else {
                PlayerActivity.binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_empty)
            }
        }
    }
}