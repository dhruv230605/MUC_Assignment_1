package com.example.mucassignment1

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PetActivity : AppCompatActivity() {

    // Mood related
    private var moodLevel = 100
    private lateinit var moodBar: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var imgPet: ImageView

    // Pet state constants (NO enum)
    private val STATE_NORMAL = 0
    private val STATE_TIRED = 1
    private val STATE_HUNGRY = 2
    private val STATE_ANGRY = 3

    private var currentState = STATE_NORMAL
    private var tapCount = 0

    private lateinit var petType: String
    private lateinit var batteryReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)

        val tvPetName = findViewById<TextView>(R.id.tvPetName)
        imgPet = findViewById(R.id.imgPet)
        tvStatus = findViewById(R.id.tvStatus)
        moodBar = findViewById(R.id.moodBar)
        val btnGoodTouch = findViewById<Button>(R.id.btnGoodTouch)
        val btnBadTouch = findViewById<Button>(R.id.btnBadTouch)

        // Intent data
        val petName = intent.getStringExtra("PET_NAME")
        petType = intent.getStringExtra("PET_TYPE") ?: "pup"

        tvPetName.text = petName ?: "My Pet"

        // Start in normal state
        updatePetImage(STATE_NORMAL)
        updateMood(0)

        // Good touch
        btnGoodTouch.setOnClickListener {
            updateMood(10)
        }

        // Bad touch
        btnBadTouch.setOnClickListener {
            updateMood(-15)
            playAngryShake(imgPet)
        }

        // Pet tapping logic (Angry state)
        imgPet.setOnClickListener {
            tapCount++
            if (tapCount > 5) {
                tvStatus.text = "Stop it! I'm angry 😡"
                updatePetImage(STATE_ANGRY)
                playAngryShake(imgPet)
            } else {
                updateMood(5)
            }
        }

        // Calm down after 10 seconds
        imgPet.postDelayed({
            tapCount = 0
            if (currentState == STATE_ANGRY) {
                tvStatus.text = "I'm calm now 🙂"
                updatePetImage(STATE_NORMAL)
            }
        }, 10_000)

        // Breathing animation
        startBreathingAnimation()

        // Tired state after 5 minutes
        imgPet.postDelayed({
            if (currentState != STATE_ANGRY && currentState != STATE_HUNGRY) {
                tvStatus.text = "I'm tired 😴"
                updatePetImage(STATE_TIRED)
            }
        }, 5 * 60 * 1000)

        // Battery receiver (Hungry state)
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: return

                if (level < 20 && currentState != STATE_ANGRY) {
                    tvStatus.text = "I'm hungry 😢"
                    updatePetImage(STATE_HUNGRY)
                } else if (level >= 20 && currentState == STATE_HUNGRY) {
                    tvStatus.text = "I'm feeling better 🙂"
                    updatePetImage(STATE_NORMAL)
                }
            }
        }
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    // Dynamically update pet image based on state + pet type
    private fun updatePetImage(state: Int) {
        currentState = state

        val imageName = when (state) {
            STATE_NORMAL -> petType
            STATE_TIRED -> "${petType}_tired"
            STATE_HUNGRY -> "${petType}_hungry"
            STATE_ANGRY -> "${petType}_angry"
            else -> petType
        }

        val resId = resources.getIdentifier(imageName, "drawable", packageName)
        if (resId != 0) {
            imgPet.setImageResource(resId)
        }
    }

    // Mood bar logic
    private fun updateMood(delta: Int) {
        moodLevel = (moodLevel + delta).coerceIn(0, 120)
        moodBar.progress = moodLevel

        val barColor = when {
            moodLevel > 70 -> Color.parseColor("#4CAF50")
            moodLevel >= 25 -> Color.parseColor("#FFC107")
            else -> Color.parseColor("#F44336")
        }
        moodBar.progressTintList = ColorStateList.valueOf(barColor)

        when {
            moodLevel < 25 && currentState != STATE_HUNGRY -> {
                tvStatus.text = "I'm very angry 😡"
                updatePetImage(STATE_ANGRY)
            }

            moodLevel >= 80 &&
                    currentState != STATE_HUNGRY &&
                    currentState != STATE_NORMAL -> {
                tvStatus.text = "I'm happy 😄"
                updatePetImage(STATE_NORMAL)
            }
        }

    }

    // Breathing animation
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
