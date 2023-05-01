package com.tech.imusic

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tech.imusic.databinding.ActivityPlayerBinding
import com.tech.imusic.fragments.SongFragment
import com.tech.imusic.model.Music

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPlayerBinding
    private var mediaPlayer:MediaPlayer ?= null

    companion object{
        var songPosition : Int = 0
        lateinit var musicArrayList:ArrayList<Music>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songPosition = intent.getIntExtra("index",0)

        when(intent.getStringExtra("class")){
            "MusicAdapter"->{
                musicArrayList = ArrayList()
                musicArrayList.addAll(SongFragment.musicArrayList)

                if(mediaPlayer == null){
                    mediaPlayer = MediaPlayer()
                }
                mediaPlayer!!.reset()
                mediaPlayer!!.setDataSource(musicArrayList[songPosition].path)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
            }
        }


    }
}