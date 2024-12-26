package com.example.mobiloodev // Kendi paket adınızı yazın

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilodev.R
import com.example.myapplication.Place

// Veriler için Experience sınıfı
data class Experience(val title: String, val rating: String, val comment: String, val imageResId: Int)

// RecyclerView için Adapter sınıfı
class ExperienceAdapter(private var experienceList: List<Place>) :
    RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder>() {

    // ViewHolder sınıfı: item_experience.xml ile bağlanır
    class ExperienceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgExperience: ImageView = itemView.findViewById(R.id.imgExperience)
        val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        val ratingTextView: TextView = itemView.findViewById(R.id.tvRating)
        val commentTextView: TextView = itemView.findViewById(R.id.tvComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_experience, parent, false)
        return ExperienceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        val experience = experienceList[position]
        holder.titleTextView.text = experience.name
        holder.ratingTextView.text = "Puan: ${experience.rating}" // Rating dinamik olarak geldi
        holder.commentTextView.text = experience.comment
        holder.imgExperience.setImageResource(experience.photoPath) // Görsel dinamik olarak geldi
    }

    override fun getItemCount(): Int {
        return experienceList.size
    }

    // Veriyi güncellemeye yarayan fonksiyon
    fun updateData(newExperienceList: List<Place>) {
        experienceList = newExperienceList
        notifyDataSetChanged() // Veriler değiştiğinde RecyclerView'in yeniden yüklenmesini sağlar
    }
}

