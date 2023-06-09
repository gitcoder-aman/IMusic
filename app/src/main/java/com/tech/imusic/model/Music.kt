package com.tech.imusic.model

data class Music(
    val id : String,
    val title : String,
    val album : String,
    val artist : String,
    val duration : Long = 0,
    val path : String,
    val artUri : String
)
class Playlist{  // only model
    lateinit var playListName : String
    lateinit var playlist: ArrayList<Music>
    lateinit var createdBy : String
    lateinit var createdOn : String
}
class MusicPlaylist{
    var ref : ArrayList<Playlist> = ArrayList()
}