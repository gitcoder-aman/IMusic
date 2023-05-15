package com.tech.imusic.services

import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.tech.imusic.MainActivity
import com.tech.imusic.PlayerActivity
import com.tech.imusic.R
import com.tech.imusic.application.ApplicationClass
import com.tech.imusic.fragments.NowPlayingFragment
import com.tech.imusic.util.Utils


class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(p0: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    override fun onCreate() {
        super.onCreate()
        // Register media button receiver with AudioManager.(bluetooth headset)
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val receiver = ComponentName(packageName, NotificationReceiver::class.java.name)
        audioManager.registerMediaButtonEventReceiver(receiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle incoming commands from Bluetooth device.(bluetooth headset)
        val action = intent!!.action
        if (action != null) {
            when (action) {
                "PLAY" -> NotificationReceiver.playMusic()
                "PAUSE" -> NotificationReceiver.pauseMusic()
                "NEXT" -> NotificationReceiver.prevNextSong(true, this)
                "PREVIOUS" -> NotificationReceiver.prevNextSong(false, this)
            }
        }

        return START_NOT_STICKY
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseBtn: Int) {

        val intent = Intent(baseContext, MainActivity::class.java) //click on notification
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val prevIntent = Intent(
            baseContext,
            NotificationReceiver::class.java
        ).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(
            baseContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val imgArt =
            Utils.getImgArt(PlayerActivity.musicArrayList[PlayerActivity.songPosition].path)
        val image = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.music_listener_image)
        }

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicArrayList[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicArrayList[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.ic_song)
            .setContentIntent(pendingIntent)
            .setLargeIcon(image)
            .setStyle(  //just set style notification style
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true) //new notification not generate
            .addAction(R.drawable.ic_previous, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
            .addAction(R.drawable.ic_exit, "Exit", exitPendingIntent)
            .build()

        //for seekbar run parallel to music play
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val playbackSpeed = if(PlayerActivity.isPlaying) 1F else 0F
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putLong(
                        MediaMetadataCompat.METADATA_KEY_DURATION,
                        mediaPlayer!!.duration.toLong()
                    )
                    .build()
            )
            val playbackState = PlaybackStateCompat.Builder()
                .setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    mediaPlayer!!.currentPosition.toLong(),
                    playbackSpeed
                )
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
            mediaSession.setPlaybackState(playbackState)
            mediaSession.setCallback(object : MediaSessionCompat.Callback() {
//                override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
//                    if(PlayerActivity.isPlaying){
//                        //pause music
//                        PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_play)
//                        NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_play)
//                        PlayerActivity.isPlaying = false
//                        mediaPlayer!!.pause()
//                        showNotification(R.drawable.ic_play)
//                    }else{
//                        //play music
//                        PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
//                        NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_pause)
//                        PlayerActivity.isPlaying = true
//                        mediaPlayer!!.start()
//                        showNotification(R.drawable.ic_pause)
//                    }
//                    return super.onMediaButtonEvent(mediaButtonEvent)
//                }
//
                //seekbar in notification move to specific position
                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    mediaPlayer!!.seekTo(pos.toInt())
                    val playbackStateNew = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer!!.currentPosition.toLong(),playbackSpeed)
                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                        .build()
                    mediaSession.setPlaybackState(playbackStateNew)
                }
            })
        }
        startForeground(13, notification)
    }

    fun createMediaPlayer() {

        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null)
                PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()

            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicArrayList[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()

            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
            PlayerActivity.musicService!!.showNotification(
                R.drawable.ic_pause_notification,
            )

            PlayerActivity.binding.seekbarStart.text =
                Utils.formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekbarEnd.text =
                Utils.formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekbar.progress = 0
            PlayerActivity.binding.seekbar.max =
                PlayerActivity.musicService!!.mediaPlayer!!.duration
            PlayerActivity.nowPlayingId =
                PlayerActivity.musicArrayList[PlayerActivity.songPosition].id

        } catch (e: Exception) {
            return
        }
    }

    fun seekBarSetup() {
        runnable = Runnable {
            PlayerActivity.binding.seekbarStart.text =
                Utils.formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekbar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            //pause music
            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_play)
            NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_play)
            PlayerActivity.isPlaying = false
            mediaPlayer!!.pause()
            showNotification(R.drawable.ic_play_notification)
            PlayerActivity.binding.lottieAnimationView.pauseAnimation()
        } else {
            //play music
            PlayerActivity.binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
            NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_pause)
            PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
            showNotification(R.drawable.ic_pause_notification)
            PlayerActivity.binding.lottieAnimationView.playAnimation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}