package com.tech.imusic

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tech.imusic.adapter.FragmentsAdapter
import com.tech.imusic.databinding.ActivityMainBinding
import com.tech.imusic.fragments.FavoriteFragment
import com.tech.imusic.fragments.PlaylistFragment
import com.tech.imusic.model.Music
import com.tech.imusic.model.MusicPlaylist
import com.tech.imusic.util.Utils


val tabArray = arrayOf(
    "Songs",
    "Favorite",
    "Playlist"
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var pagerAdapter: FragmentsAdapter

    private var appUpdate: AppUpdateManager?= null
    private val REQUEST_CODE = 100

    private val tabIcons = intArrayOf(
        R.drawable.ic_song,
        R.drawable.ic_favorite,
        R.drawable.ic_playlist
    )

    @SuppressLint("QueryPermissionsNeeded")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        requestRuntimePermission()
        adjustFontScale(resources.configuration)  //Lock font size of system setting

        setContentView(binding.root)

        appUpdate = AppUpdateManagerFactory.create(this)
        checkUpdate()

//        val intent = Intent(this, MusicService::class.java)
//        bindService(intent, this, BIND_AUTO_CREATE)
//        startService(intent)

        FavoriteFragment.favoriteList = ArrayList()
        //for retrieve favorites data using shared preferences
        val editor = getSharedPreferences("FAVORITES_PLAYLIST", MODE_PRIVATE)
        val jsonString = editor.getString("FavoriteSongs",null)
        val typeToken = object:TypeToken<ArrayList<Music>>(){}.type

        if(jsonString != null){
            val favoriteListDataSharedPref : ArrayList<Music>  = GsonBuilder().create().fromJson(jsonString,typeToken)
            FavoriteFragment.favoriteList.addAll(favoriteListDataSharedPref)
        }
        PlaylistFragment.musicPlaylist = MusicPlaylist()
        //for retrieve favorites data using shared preferences
        val jsonStringPlaylist = editor.getString("MusicPlaylist",null)

        if(jsonStringPlaylist != null){
            val playlistListDataSharedPref : MusicPlaylist  = GsonBuilder().create().fromJson(jsonStringPlaylist,MusicPlaylist::class.java)
            PlaylistFragment.musicPlaylist = playlistListDataSharedPref
        }

        //for retrieve last Playing song data using shared preferences
    /*    PlayerActivity.musicArrayList = ArrayList()
        val ed = getSharedPreferences("NOW_PLAYING_SONG", MODE_PRIVATE)
        val json = ed.getString("NowPlayingSong",null)
        val typeToken1 = object: TypeToken<ArrayList<Music>>(){}.type

        if(json != null){
            val data :ArrayList<Music> = GsonBuilder().create().fromJson(json,typeToken1)
            Log.d("@@@@", "data$data")
            PlayerActivity.musicArrayList.addAll(data)
        }
        Log.d("@@@@","size of Player "+PlayerActivity.musicArrayList.size) */

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setTitle(R.string.search_your_music)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white, theme);

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        pagerAdapter = FragmentsAdapter(supportFragmentManager, lifecycle)
        viewPager2 = binding.viewPager
        tabLayout = binding.tableLayout
        viewPager2.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager2, true) { tab, index ->
            tab.text = tabArray[index]
            tab.setIcon(tabIcons[index])
        }.attach()

        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menu_rate -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+this.packageName)))
                }
                R.id.menu_share ->{
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(
                        Intent.EXTRA_TEXT, "IMusic - Free and unlimited Music app!\n" +
                                "Get unlimited access to millions of music, curated playlists, and content from your favorite artists. This is the music app for you to listen to online and offline music." +
                                "https://play.google.com/store/apps/details?id=" + this.packageName
                    )
                    sendIntent.type = "text/plain"

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
                R.id.menu_about -> startActivity(Intent(baseContext,AboutActivity::class.java))
                R.id.menu_developer -> {
                    val popupMenu = PopupMenu(this, findViewById(R.id.menu_developer),Gravity.CENTER, R.style.MyPopupMenuStyle, 0)
                    popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { popupMenuItem ->
                        when (popupMenuItem.itemId) {
                            R.id.instagram_item -> {
                               Utils.instagramOpen(this)
                                true
                            }
                            R.id.linkedin_item -> {
                                Utils.linkedinOpen(this)
                                true
                            }
                            else -> false
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        popupMenu.setForceShowIcon(true)
                    } // display icons
                    popupMenu.show()
                }
                R.id.menu_feedback->{
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Feedback")
                    builder.setMessage("Want to Open Gmail app?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:aman.nittc@gmail.com")
                            }
                            startActivity(intent)
                            dialog.dismiss()
                        }.setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                }
            }
            true
        }
        binding.toolbar.setOnClickListener {
            val intent = Intent(this,SearchViewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUpdate() {
        appUpdate?.appUpdateInfo?.addOnSuccessListener { updateInfo->
            if(updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                appUpdate?.startUpdateFlowForResult(updateInfo,AppUpdateType.IMMEDIATE,this,REQUEST_CODE)
            }
        }
    }
    private fun inProgressUpdate(){
        appUpdate?.appUpdateInfo?.addOnSuccessListener { updateInfo->
            if(updateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                appUpdate?.startUpdateFlowForResult(updateInfo,AppUpdateType.IMMEDIATE,this,REQUEST_CODE)
            }
        }
    }

    private fun requestRuntimePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                tabLayout = binding.tableLayout
                viewPager2.adapter = pagerAdapter
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!PlayerActivity.isPlaying && PlayerActivity.musicService != null) {
            Utils.exitApplication()
        }

    }

    override fun onResume() {
        super.onResume()
        inProgressUpdate()

        //for storing favorites data using shared preferences
        val editor = getSharedPreferences("FAVORITES_PLAYLIST", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavoriteFragment.favoriteList)
        editor.putString("FavoriteSongs",jsonString)

        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistFragment.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)
        editor.apply()

        //for storing nowPlaying data using shared preferences
      /*  val editor1 = getSharedPreferences("NOW_PLAYING_SONG", MODE_PRIVATE).edit()
        val jsonString1 = GsonBuilder().create().toJson(SongFragment.musicArrayList)
        editor1.putString("NowPlayingSong",jsonString1)
        editor1.apply()
        Log.d("@@@@","Destroy"+SongFragment.musicArrayList.toString())  */
    }
    fun adjustFontScale(configuration: Configuration) {
        configuration.fontScale = 1.0.toFloat()
        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }


//    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
//        val binder = service as MusicService.MyBinder
//        PlayerActivity.musicService = binder.currentService()
//
//        Log.d("@@@@",PlayerActivity.musicService.toString())
//
//    }
//
//    override fun onServiceDisconnected(p0: ComponentName?) {
//        PlayerActivity.musicService = null
//    }
}