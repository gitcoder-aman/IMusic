package com.tech.imusic.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tech.imusic.PlayerActivity
import com.tech.imusic.R
import com.tech.imusic.databinding.FragmentNowPlayingBinding
import com.tech.imusic.util.Utils

class NowPlayingFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNowPlayingBinding.inflate(layoutInflater, container, false)

        binding.playPauseBtnFrag.setOnClickListener {
            if (PlayerActivity.isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
        binding.nextBtnFrag.setOnClickListener {
            Utils.setSongPosition(increment = true)

            PlayerActivity.musicService?.createMediaPlayer()

            Glide.with(this)
                .load(PlayerActivity.musicArrayList[PlayerActivity.songPosition].artUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.music_listener_image)
                        .centerCrop()
                )
                .into(binding.songImgFrag)
            binding.songNameFrag.text =
                PlayerActivity.musicArrayList[PlayerActivity.songPosition].title
            PlayerActivity.binding.songName.text =
                PlayerActivity.musicArrayList[PlayerActivity.songPosition].title

            playMusic()
        }
        binding.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class", "NowPlaying")
            context?.startActivity(intent)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null) {

            binding.songNameFrag.isSelected = true
            Glide.with(this)
                .load(PlayerActivity.musicArrayList[PlayerActivity.songPosition].artUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.music_listener_image)
                        .centerCrop()
                )
                .into(binding.songImgFrag)

            binding.songNameFrag.text =
                PlayerActivity.musicArrayList[PlayerActivity.songPosition].title
            if (PlayerActivity.isPlaying) {
                binding.playPauseBtnFrag.setIconResource(R.drawable.ic_pause)
            } else {
                binding.playPauseBtnFrag.setIconResource(R.drawable.ic_play)
            }
        }

    }

    private fun playMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnFrag.setIconResource(R.drawable.ic_pause)
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause_notification)
        PlayerActivity.binding.nextBtn.setIconResource(R.drawable.ic_pause)
        PlayerActivity.isPlaying = true
        PlayerActivity.binding.lottieAnimationView.playAnimation()
    }

    private fun pauseMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnFrag.setIconResource(R.drawable.ic_play)
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_play_notification)
        PlayerActivity.binding.nextBtn.setIconResource(R.drawable.ic_play)
        PlayerActivity.isPlaying = false
        PlayerActivity.binding.lottieAnimationView.pauseAnimation()
    }
}