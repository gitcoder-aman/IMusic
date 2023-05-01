package com.tech.imusic

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.tech.imusic.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({ // on below line we are
            // creating a new intent
            val i = Intent(this@SplashActivity, MainActivity::class.java)

            startActivity(i)
            finish()
        }, 2000)


    }
}