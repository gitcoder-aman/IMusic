package com.tech.imusic.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Audio.Media
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tech.imusic.PlayerActivity
import com.tech.imusic.adapter.MusicAdapter
import com.tech.imusic.databinding.FragmentSufflerBinding
import com.tech.imusic.model.Music
import java.io.File

class SongFragment : Fragment() {

    private lateinit var binding:FragmentSufflerBinding
    private lateinit var musicAdapter:MusicAdapter
    companion object {
        var musicArrayList: ArrayList<Music> = ArrayList()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSufflerBinding.inflate(layoutInflater,container,false)

        binding.musicRecycler.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)

        getAllAudio()

        musicAdapter = MusicAdapter(requireActivity(),musicArrayList)
        binding.musicRecycler.adapter = musicAdapter

        binding.musicRecycler.setHasFixedSize(true)
        binding.musicRecycler.setItemViewCacheSize(13)
        binding.totalSong.text = "Total Songs: "+musicAdapter.itemCount

        binding.shuffleBtn.setOnClickListener {
            val intent = Intent(activity, PlayerActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    @SuppressLint("Recycle")
    private fun getAllAudio(){
        val selection = Media.IS_MUSIC + "!= 0"    // Media = MediaStore.Audio.Media
        val projection = arrayOf(Media._ID,Media.TITLE,Media.ALBUM,Media.ARTIST,Media.DURATION,Media.DATE_ADDED,Media.DATA,Media.ALBUM_ID)

        val cursor = activity?.contentResolver?.query(Media.EXTERNAL_CONTENT_URI,projection,selection,null,Media.DATE_ADDED+" DESC",null)
        if(cursor != null ){
            if(cursor.moveToFirst()){
                do {
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(Media.TITLE))
                    val id = cursor.getString(cursor.getColumnIndexOrThrow(Media._ID))
                    val album = cursor.getString(cursor.getColumnIndexOrThrow(Media.ALBUM))
                    val artist = cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST))
                    val path = cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))
                    val duration = cursor.getLong(cursor.getColumnIndexOrThrow(Media.DURATION))
                    val albumId = cursor.getLong(cursor.getColumnIndexOrThrow(Media.ALBUM_ID)).toString()

                    val uri = Uri.parse("content://media/external/audio/albumart")

                    val artUri = Uri.withAppendedPath(uri,albumId).toString()

                    val music = Music(id = id ,title = title,album = album,artist = artist,duration = duration,path = path, artUri = artUri)
                    val file = File(music.path)
                    if(file.exists()){
                        musicArrayList.add(music)
                    }
                }while (cursor.moveToNext())
                cursor.close()
            }
        }
    }

}