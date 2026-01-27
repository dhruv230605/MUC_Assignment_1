package com.example.mucassignment1

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)

        val tvPetName = findViewById<TextView>(R.id.tvPetName)
        val imgPet = findViewById<ImageView>(R.id.imgPet)

        // Receive data from Intent
        val petName = intent.getStringExtra("PET_NAME")
        val petImage = intent.getIntExtra("PET_IMAGE", -1)

        // Set UI
        tvPetName.text = petName ?: "My Pet"

        if (petImage != -1) {
            imgPet.setImageResource(petImage)
        }
    }
}
