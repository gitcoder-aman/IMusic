package com.tech.imusic.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.tech.imusic.MainActivity
import com.tech.imusic.PlayerActivity
import com.tech.imusic.R
import com.tech.imusic.application.ApplicationClass
import com.tech.imusic.fragments.NowPlayingFragment
import com.tech.imusic.util.Utils

class MusicService : Service(),AudioManager.OnAudioFocusChangeListener {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession:MediaSessionCompat
    private lateinit var runnable:Runnable
     lateinit var audioManager: AudioManager

    override fun onBind(p0: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext,"My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseBtn:Int) {

        val intent = Intent(baseContext,MainActivity::class.java) //click on notification
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val prevIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val imgArt = Utils.getImgArt(PlayerActivity.musicArrayList[PlayerActivity.songPosition].path)
        val image = if(imgArt != null){
            BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
        }else{
            BitmapFactory.decodeResource(resources, R.drawable.music_listener_image)
        }

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicArrayList[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicArrayList[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.ic_song)
            .setContentIntent(pendingIntent)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true) //new notification not generate
            .addAction(R.drawable.ic_previous, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
            .addAction(R.drawable.ic_exit, "Exit", exitPendingIntent)
            .build()

        startForeground(13,notification)
    }
     fun createMediaPlayer() {

        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null)
                PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()

            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicArrayList[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()

            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause_notification)

            PlayerActivity.binding.seekbarStart.text = Utils.formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekbarEnd.text = Utils.formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekbar.progress = 0
            PlayerActivity.binding.seekbar.max = PlayerActivity.musicService!!.mediaPlayer!!.duration
            PlayerActivity.nowPlayingId = PlayerActivity.musicArrayList[PlayerActivity.songPosition].id

        } catch (e: Exception) {
            return
        }
    }
     fun seekBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.seekbarStart.text = Utils.formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekbar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange <= 0){
            //pause music
            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_play)
            NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_play)
            PlayerActivity.isPlaying = false
            mediaPlayer!!.pause()
            showNotification(R.drawable.ic_play_notification)
            PlayerActivity.binding.lottieAnimationView.pauseAnimation()
        }else{
            //play music
            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
            NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_pause)
            PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
            showNotification(R.drawable.ic_pause_notification)
            PlayerActivity.binding.lottieAnimationView.playAnimation()
        }
    }
}