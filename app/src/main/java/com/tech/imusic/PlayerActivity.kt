package com.tech.imusic

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tech.imusic.databinding.ActivityPlayerBinding
import com.tech.imusic.fragments.FavoriteFragment
import com.tech.imusic.fragments.NowPlayingFragment
import com.tech.imusic.fragments.PlaylistFragment
import com.tech.imusic.fragments.SongFragment
import com.tech.imusic.model.Music
import com.tech.imusic.services.MusicService
import com.tech.imusic.util.Utils

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    private var AUDIO_TYPE = "audio/*"
    private var classIntentString:String ?= ""
    companion object {
        var songPosition: Int = 0
        var musicArrayList: ArrayList<Music> = ArrayList()
        var musicService: MusicService? = null
        var isPlaying: Boolean = false

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeatMusic: Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
        var nowPlayingId:String = ""
        var isFavorite:Boolean = false
        var fIndex:Int = -1
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classIntentString = intent.getStringExtra("class")
        if(classIntentString!="NowPlaying"){
            //for starting service
            val intent = Intent(this, MusicService::class.java)
            bindService(intent, this, BIND_AUTO_CREATE)
            startService(intent)
        }
        initializeLayout()

        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.favoriteBtn.setOnClickListener {
            if(isFavorite){
                binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_empty)
                isFavorite = false
                FavoriteFragment.favoriteList.removeAt(fIndex)
            }else{
                binding.favoriteBtn.setImageResource(R.drawable.ic_favorite)
                isFavorite = true
                FavoriteFragment.favoriteList.add(musicArrayList[songPosition])
                if(FavoriteFragment.adapter != null)
                FavoriteFragment.adapter!!.notifyDataSetChanged()
            }
            if(FavoriteFragment.binding != null){
                if(FavoriteFragment.favoriteList.size < 1){
                    FavoriteFragment.binding?.favoriteShuffle?.visibility = View.INVISIBLE
                }else{
                    FavoriteFragment.binding?.favoriteShuffle?.visibility = View.VISIBLE
                }
            }
        }
        binding.playPauseBtn.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
        binding.previousBtn.setOnClickListener {
            prevNextSong(false)
        }
        binding.nextBtn.setOnClickListener {
            prevNextSong(true)
        }
        binding.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService!!.mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit

        })
        binding.repeatBtn.setOnClickListener {
            if (!repeatMusic) {
                repeatMusic = true
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.cool_Pink))
                Toast.makeText(this, "Repeat on", Toast.LENGTH_SHORT).show()
            } else {
                repeatMusic = false
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.white))
                Toast.makeText(this, "Repeat off", Toast.LENGTH_SHORT).show()
            }
        }
        binding.equilizerBtn.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayer!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                someActivityResultLauncher.launch(eqIntent)

            } catch (e: java.lang.Exception) {
                Toast.makeText(this, "Equalizer Features not Supported!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.timerBtn.setOnClickListener {
            val timer = min15 || min30 || min60
            if (!timer) showBottomSheetDialog()
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Do you want to stop timer?")
                    .setPositiveButton("Yes") { _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.timerBtn.setColorFilter(ContextCompat.getColor(this, R.color.white))
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
            }
        }
        binding.shareBtn.setOnClickListener { //music share
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = AUDIO_TYPE
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicArrayList[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))
        }
    }

//    @SuppressLint("Recycle")
//    private fun getMusicDetails(contentUri : Uri): Music {
//
//        var cursor:Cursor ?= null
////        val selection = Media.IS_MUSIC + "!= 0"
//        musicArrayList.clear()
//
//        try{
//            val projection = arrayOf(Media.DATA,Media.DURATION)
//             cursor = this.contentResolver.query(Media.EXTERNAL_CONTENT_URI,projection,null,null,null)
//            val dataColumn = cursor?.getColumnIndexOrThrow(Media.DATA)
//            val durationColumn = cursor?.getColumnIndexOrThrow(Media.DURATION)
//
////            if(cursor != null) {
//                cursor!!.moveToFirst()
//
////                val path = cursor?.getString(cursor.getColumnIndexOrThrow(Media.DATA))
////                val duration = cursor?.getLong(cursor.getColumnIndexOrThrow(Media.DURATION))
//
//            val path = dataColumn?.let { cursor?.getString(it) }
//            val duration = durationColumn?.let { cursor?.getLong(it) }!!
//
//                Log.d("@@@@", path.toString())
//                Log.d("@@@@", duration.toString())
////            }
//            return Music(
//                "Unknown",
//                path.toString(),
//                "Unknown",
//                "Unknown",
//                20,
//                "path.toString()",
//                "Unknown"
//            )
//        }finally {
//            cursor?.close()
//        }
//    }

    override fun onResume() {
        super.onResume()
            binding.songName.isSelected = true
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0) //song position is store in the songPosition

        when (classIntentString) {
            "MusicAdapter" -> {
                musicArrayList = ArrayList()
                musicArrayList.addAll(SongFragment.musicArrayList)
                setLayout()
            }
            "FavoriteAdapter"->{
                musicArrayList = ArrayList()
                musicArrayList.addAll(FavoriteFragment.favoriteList)
                setLayout()
            }

            "SongFragmentShuffle" -> {
                musicArrayList = ArrayList()
                musicArrayList.addAll(SongFragment.musicArrayList)
                musicArrayList.shuffle()
                setLayout()
            }
            "FavoriteFragmentShuffle" -> {
                musicArrayList = ArrayList()
                musicArrayList.addAll(FavoriteFragment.favoriteList)
                musicArrayList.shuffle()
                setLayout()
            }
            "PlaylistShuffle" -> {
                musicArrayList = ArrayList()
                musicArrayList.addAll(PlaylistDetailActivity.playlistList)
                musicArrayList.shuffle()
                setLayout()
            }
            "PlaylistDetailsAdapter" -> {
                musicArrayList = ArrayList()
                musicArrayList.addAll(PlaylistFragment.musicPlaylist.ref[PlaylistDetailActivity.currentPlaylistPos].playlist)
                setLayout()
            }

            "MusicSearchViewActivity" -> {
                musicArrayList = ArrayList()
                musicArrayList.addAll(SearchViewActivity.filteredMusic)
                setLayout()
            }
            "NowPlaying" -> {
                setLayout()
                if(isPlaying){
                    binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
                    binding.lottieAnimationView.playAnimation()
                }else{
                    binding.playPauseBtn.setIconResource(R.drawable.ic_play)
                    binding.lottieAnimationView.pauseAnimation()
                }
                binding.seekbarStart.text =
                    Utils.formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.seekbarEnd.text =
                    Utils.formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekbar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekbar.max = musicService!!.mediaPlayer!!.duration
            }
        }
    }

    private fun setLayout() {

        fIndex = Utils.favoriteChecker(musicArrayList[songPosition].id)
        if(isFavorite){
            binding.favoriteBtn.setImageResource(R.drawable.ic_favorite)
        }else{
            binding.favoriteBtn.setImageResource(R.drawable.ic_favorite_empty)
        }
        try {
            Glide.with(this)
                .load(musicArrayList[songPosition].artUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.music_listener_image)
                        .centerCrop()
                )
                .into(binding.songImageView)

            binding.songName.text = musicArrayList[songPosition].title
            if (repeatMusic) {
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.cool_Pink))
            }
            if (min15 || min30 || min60) binding.timerBtn.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.cool_Pink
                )
            )
        } catch (e: Exception) {
            return
        }
    }

    private fun createMediaPlayer() {

        try {
            if (musicService!!.mediaPlayer == null)
                musicService!!.mediaPlayer = MediaPlayer()


            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicArrayList[songPosition].path)
            Log.d("@@@@","path: ${musicArrayList[songPosition].path}")
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
            musicService!!.showNotification(R.drawable.ic_pause_notification)

            binding.seekbarStart.text =
                Utils.formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.seekbarEnd.text =
                Utils.formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekbar.progress = 0
            binding.seekbar.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicArrayList[songPosition].id

        } catch (e: Exception) {
            return
        }
    }

    private fun playMusic() {
        binding.playPauseBtn.setIconResource(R.drawable.ic_pause)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
        musicService!!.showNotification(R.drawable.ic_pause_notification)
        binding.lottieAnimationView.playAnimation()
    }

    private fun pauseMusic() {
        binding.playPauseBtn.setIconResource(R.drawable.ic_play)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        musicService!!.showNotification(R.drawable.ic_play_notification)
        binding.lottieAnimationView.pauseAnimation()
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            Utils.setSongPosition(true)
            setLayout()
            createMediaPlayer()
        } else {
            Utils.setSongPosition(false)
            setLayout()
            createMediaPlayer()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()
        SongFragment.musicAdapter!!.notifyDataSetChanged() // for text color change update so adapter notify
        FavoriteFragment.adapter?.notifyDataSetChanged()

        //for Handling Calls & Other Audio Changes
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Utils.setSongPosition(true)
        setLayout()
        createMediaPlayer()
        updateFragmentLayout()
    }

    private fun updateFragmentLayout() {
        if (musicService != null) {

            NowPlayingFragment.binding.songNameFrag.isSelected = true

            NowPlayingFragment.binding.songNameFrag.text =
                musicArrayList[songPosition].title
            if (isPlaying) {
                NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_pause)
            } else {
                NowPlayingFragment.binding.playPauseBtnFrag.setIconResource(R.drawable.ic_play)
            }
        }
    }

    private val someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                return@registerForActivityResult
            }
        }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min15Layout)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 15 minutes", Toast.LENGTH_SHORT)
                .show()
            binding.timerBtn.setColorFilter(ContextCompat.getColor(this, R.color.cool_Pink))
            min15 = true
            Thread {
                Thread.sleep(15 * 60000)
                if (min15) {
                    Utils.exitApplication()
                }
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min30Layout)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 30 minutes", Toast.LENGTH_SHORT)
                .show()
            binding.timerBtn.setColorFilter(ContextCompat.getColor(this, R.color.cool_Pink))
            min30 = true
            Thread {
                Thread.sleep(30 * 60000)
                if (min30) {
                    Utils.exitApplication()
                }
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min60Layout)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 60 minutes", Toast.LENGTH_SHORT)
                .show()
            binding.timerBtn.setColorFilter(ContextCompat.getColor(this, R.color.cool_Pink))
            min60 = true
            Thread {
                Thread.sleep(60 * 60000)
                if (min60) {
                    Utils.exitApplication()
                }
            }.start()
            dialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(musicArrayList[songPosition].id == "Unknown" && !isPlaying){
            Utils.exitApplication()
        }
    }

}