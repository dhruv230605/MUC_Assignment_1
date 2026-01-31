package com.example.mucassignment1

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PetActivity : AppCompatActivity() {

    private var tapCount = 0
    private lateinit var batteryReceiver: android.content.BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)

        val tvPetName = findViewById<TextView>(R.id.tvPetName)
        val imgPet = findViewById<ImageView>(R.id.imgPet)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val btnTap = findViewById<Button>(R.id.btnTap)

        // Intent data
        val petName = intent.getStringExtra("PET_NAME")
        val petImage = intent.getIntExtra("PET_IMAGE", -1)
        val originalImage = petImage

        tvPetName.text = petName ?: "My Pet"
        if (petImage != -1) {
            imgPet.setImageResource(petImage)
        }

        // Tap logic
        val tapListener = {
            tapCount++

            if (tapCount <= 5) {
                tvStatus.text = "Hehe 😄 ($tapCount)"
            } else {
                tvStatus.text = "I'm angry 😡"
                imgPet.setColorFilter(
                    resources.getColor(android.R.color.holo_red_light),
                    android.graphics.PorterDuff.Mode.MULTIPLY
                )
            }
        }

        imgPet.setOnClickListener { tapListener() }
        btnTap.setOnClickListener { tapListener() }

        batteryReceiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
                val level = intent?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: return

                if (level < 20) {
                    showHungryState(tvStatus, imgPet)
                } else {
                    showHappyState(tvStatus, imgPet, originalImage)
                }
            }
        }
        registerReceiver(
            batteryReceiver,
            android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
        )


    }
    private fun showHappyState(tvStatus: TextView, imgPet: ImageView, originalImage: Int) {
        tvStatus.text = "I'm happy 😊"
        imgPet.clearColorFilter()
        imgPet.setImageResource(originalImage)
    }

    private fun showHungryState(tvStatus: TextView, imgPet: ImageView) {
        tvStatus.text = "I'm hungry 😢"
        imgPet.setColorFilter(
            resources.getColor(android.R.color.holo_orange_light),
            android.graphics.PorterDuff.Mode.MULTIPLY
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
    }


}
