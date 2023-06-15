package com.masdika.qrcodegenerator.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.masdika.qrcodegenerator.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Always light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_splash_screen)

        //Set Background Animation
        val bgImage: ImageView = findViewById(R.id.splash_image)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.slide)
        bgImage.startAnimation(slideAnimation)

        @Suppress("DEPRECATION")
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }
}