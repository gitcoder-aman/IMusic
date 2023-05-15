package com.tech.imusic

import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tech.imusic.util.Utils


open class MusicRunFileActivity : AppCompatActivity() {

    private lateinit var runnable: Runnable

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.data?.scheme.contentEquals("content")) {
            showAudioPlayerDialog(intent.data)
        }
    }

    private fun showAudioPlayerDialog(audioUri: Uri?) {

        val mediaPlayer = MediaPlayer.create(this, audioUri)
        mediaPlayer.start()
        val customDialog =
            LayoutInflater.from(this).inflate(R.layout.music_layout_in_filemanager, null, false)

        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("IMusic")
        builder.setView(customDialog)
        builder.setCancelable(false)
        builder.setPositiveButton("close") { dialog, _ ->
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
            dialog.dismiss()
            finish()
        }.show()

        val titleText = customDialog.findViewById<TextView>(R.id.dialogTitle)
        val seekbarStartText = customDialog.findViewById<TextView>(R.id.dialogSeekbarStart)
        val seekbarEndText = customDialog.findViewById<TextView>(R.id.dialogSeekbarEnd)
        val seekBar = customDialog.findViewById<SeekBar>(R.id.dialogSeekbar)
        val playPauseBtn = customDialog.findViewById<ImageButton>(R.id.dialogPlayPauseBtn)

        playPauseBtn?.setImageResource(R.drawable.ic_pause)
        seekbarEndText?.text = Utils.formatDuration(mediaPlayer!!.duration.toLong())


//        titleText.text = getAudioTitleFromUri(audioUri!!)
        seekBar?.max = mediaPlayer.duration
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        playPauseBtn?.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playPauseBtn.setImageResource(R.drawable.ic_play)
            } else {
                mediaPlayer.start()
                playPauseBtn.setImageResource(R.drawable.ic_pause)
            }
        }

        runnable = Runnable {
            seekBar?.progress = mediaPlayer.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
            seekbarStartText?.text = Utils.formatDuration(mediaPlayer.currentPosition.toLong())
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    private fun getAudioTitleFromUri(audioUri: Uri): String? {
        var title: String? = null
        val cursor = contentResolver.query(audioUri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            title = cursor.getString(titleIndex)
            cursor.close()
        }
        return title
    }

}