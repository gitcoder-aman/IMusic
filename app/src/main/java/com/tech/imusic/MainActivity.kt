package com.tech.imusic

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tech.imusic.adapter.FragmentsAdapter
import com.tech.imusic.databinding.ActivityMainBinding
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

    private val tabIcons = intArrayOf(
        R.drawable.ic_song,
        R.drawable.ic_favorite,
        R.drawable.ic_playlist
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        requestRuntimePermission()
        setContentView(binding.root)

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
                R.id.menu_feedback -> alertDialog()
                R.id.menu_setting -> Toast.makeText(baseContext, "Setting", Toast.LENGTH_SHORT).show()
                R.id.menu_about -> Toast.makeText(baseContext, "Setting", Toast.LENGTH_SHORT).show()
                R.id.menu_developer -> Toast.makeText(baseContext, "developer", Toast.LENGTH_SHORT).show()
            }
            true
        }
        binding.toolbar.setOnClickListener {
            val intent = Intent(this,SearchViewActivity::class.java)
            startActivity(intent)
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

    fun alertDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Execute")
            .setMessage("Do you want to close app?")
            .setPositiveButton("Yes") { _, _ ->
                Utils.exitApplication()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val customDialog = builder.create()
        customDialog.show()
    }
}