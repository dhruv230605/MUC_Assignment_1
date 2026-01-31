package com.example.mucassignment1

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.BatteryManager
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PetActivity : AppCompatActivity() {

    private var moodLevel = 100 // Start at neutral
    private lateinit var moodBar: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var imgPet: ImageView
    private var originalImage: Int = -1

    private lateinit var batteryReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)

        val tvPetName = findViewById<TextView>(R.id.tvPetName)
        imgPet = findViewById<ImageView>(R.id.imgPet)
        tvStatus = findViewById<TextView>(R.id.tvStatus)
        moodBar = findViewById<ProgressBar>(R.id.moodBar)
        val btnGoodTouch = findViewById<Button>(R.id.btnGoodTouch)
        val btnBadTouch = findViewById<Button>(R.id.btnBadTouch)

        // Intent data
        val petName = intent.getStringExtra("PET_NAME")
        originalImage = intent.getIntExtra("PET_IMAGE", -1)

        tvPetName.text = petName ?: "My Pet"
        if (originalImage != -1) {
            imgPet.setImageResource(originalImage)
        }

        // Initial Mood Update
        updateMood(0)

        // Button Actions
        btnGoodTouch.setOnClickListener {
            updateMood(10) // Increase mood
        }

        btnBadTouch.setOnClickListener {
            updateMood(-15) // Decrease mood
            playAngryShake(imgPet)
        }

        imgPet.setOnClickListener {
            updateMood(5) // Tapping the pet gently is a good touch
        }

        // Breathing Animation
        startBreathingAnimation()

        // Battery Receiver
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: return
                if (level < 20) {
                    tvStatus.text = "I'm hungry and weak 😢"
                    imgPet.setColorFilter(
                        ContextCompat.getColor(this@PetActivity, android.R.color.holo_orange_light),
                        PorterDuff.Mode.MULTIPLY
                    )
                }
            }
        }
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun updateMood(delta: Int) {
        moodLevel += delta
        moodLevel = moodLevel.coerceIn(0, 120)
        moodBar.progress = moodLevel

        // Dynamic Color for the Mood Bar
        val barColor = when {
            moodLevel > 70 -> Color.parseColor("#4CAF50") // Green
            moodLevel >= 25 -> Color.parseColor("#FFC107") // Yellow/Orange
            else -> Color.parseColor("#F44336") // Red
        }
        moodBar.progressTintList = ColorStateList.valueOf(barColor)

        when {
            moodLevel > 100 -> {
                tvStatus.text = "I'm super happy! 😄"
                imgPet.clearColorFilter()
            }
            moodLevel > 80 -> {
                tvStatus.text = "I'm doing great! 😊"
                imgPet.clearColorFilter()
            }
            moodLevel >= 25 -> {
                tvStatus.text = "I'm feeling okay 😐"
                imgPet.clearColorFilter()
            }
            else -> {
                tvStatus.text = "I'm very angry! 😡"
                imgPet.setColorFilter(
                    ContextCompat.getColor(this, android.R.color.holo_red_light),
                    PorterDuff.Mode.MULTIPLY
                )
            }
        }
    }

    private fun startBreathingAnimation() {
        val scaleX = ObjectAnimator.ofFloat(imgPet, "scaleX", 1f, 1.05f)
        val scaleY = ObjectAnimator.ofFloat(imgPet, "scaleY", 1f, 1.05f)

        scaleX.duration = 1000
        scaleY.duration = 1000

        scaleX.repeatCount = ValueAnimator.INFINITE
        scaleY.repeatCount = ValueAnimator.INFINITE

        scaleX.repeatMode = ValueAnimator.REVERSE
        scaleY.repeatMode = ValueAnimator.REVERSE

        scaleX.start()
        scaleY.start()
    }

    private fun playAngryShake(imgPet: ImageView) {
        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)
        imgPet.startAnimation(shake)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
    }
}
