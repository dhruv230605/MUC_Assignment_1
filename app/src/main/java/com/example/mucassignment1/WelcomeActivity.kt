package com.example.mucassignment1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.jvm.java

class WelcomeActivity : AppCompatActivity() {

    private lateinit var selectedPet: Pet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val etPetName = findViewById<EditText>(R.id.etPetName)
        val rvPets = findViewById<RecyclerView>(R.id.rvPets)
        val btnStart = findViewById<Button>(R.id.btnStart)

        val petList = listOf(
            Pet("Dog", R.drawable.ic_dog),
            Pet("Cat", R.drawable.ic_cat),
            Pet("Panda", R.drawable.ic_panda),
            Pet("Dino", R.drawable.ic_dino)
        )

        val adapter = PetAdapter(petList) { pet ->
            selectedPet = pet
        }

        rvPets.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvPets.adapter = adapter

        btnStart.setOnClickListener {
            val petName = etPetName.text.toString().trim()

            if (petName.isEmpty()) {
                Toast.makeText(this, "Please enter pet name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!::selectedPet.isInitialized) {
                Toast.makeText(this, "Please select a pet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, PetActivity::class.java)
            intent.putExtra("PET_NAME", petName)
            intent.putExtra("PET_IMAGE", selectedPet.imageResId)
            startActivity(intent)
        }
    }
}
