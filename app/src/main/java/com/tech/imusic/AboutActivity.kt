package com.tech.imusic

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.tech.imusic.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAboutBinding

    @SuppressLint("UseCompatLoadingForDrawables", "QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.aboutToolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.aboutToolbar.title = "About"

        // Set the back button color

        val backArrow =
            resources.getDrawable(R.drawable.ic_arrow_back,theme) // Replace with your own drawable

        backArrow.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        binding.aboutToolbar.navigationIcon = backArrow

        binding.instagramBtn.setOnClickListener {
            val username = resources.getString(R.string.insta_id)
            val uri = Uri.parse("http://instagram.com/_u/$username")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.instagram.android")

// Check if Instagram app is installed
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // Instagram app is not installed, open in browser
                val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(browserIntent)
            }
        }
        binding.linkedinBtn.setOnClickListener {
            val profileId = resources.getString(R.string.linkedin_id)
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse("linkedin://profile/$profileId")
            intent.data = uri
            intent.setPackage("com.linkedin.android")

// Check if LinkedIn app is installed
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // LinkedIn app is not installed, open in browser
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/profile/view?id=$profileId"))
                startActivity(browserIntent)
            }
        }
        binding.githubBtn.setOnClickListener {
            val url = "https://github.com/gitcoder-aman"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Perform the same action as the system back button
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}