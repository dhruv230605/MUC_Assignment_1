package com.example.mucassignment1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PetAdapter(
    private val pets: List<Pet>,
    private val onPetClick: (Pet) -> Unit
) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    private var selectedPosition = -1

    inner class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val petImage: ImageView = itemView.findViewById(R.id.imgPet)
        val petName: TextView = itemView.findViewById(R.id.tvPetName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pet, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = pets[position]

        holder.petName.text = pet.name
        holder.petImage.setImageResource(pet.imageResId)

        // Highlight selected pet
        holder.itemView.alpha = if (position == selectedPosition) 1.0f else 0.5f

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onPetClick(pet)
        }

    }

    override fun getItemCount(): Int = pets.size
}