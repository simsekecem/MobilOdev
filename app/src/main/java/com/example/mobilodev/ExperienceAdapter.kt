package com.example.mobilodev // Kendi paket adınızı yazın

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Place




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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        val experience = experienceList[position]
        holder.titleTextView.text = experience.name
        holder.ratingTextView.text = "Puan: ${experience.rating}" // Rating dinamik olarak geldi
        holder.commentTextView.text = experience.comment
        holder.imgExperience.setImageURI(android.net.Uri.parse(experience.photoPath))

    }

    override fun getItemCount(): Int {
        return experienceList.size
    }


}