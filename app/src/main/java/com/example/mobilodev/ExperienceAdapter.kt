package com.example.mobilodev

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Place
import com.example.myapplication.DatabaseHelper
import java.io.File

class ExperienceAdapter(private var experienceList: List<Place>) :
    RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder>() {

    // ViewHolder sınıfı: item_experience.xml ile bağlanır
    class ExperienceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgExperience: ImageView = itemView.findViewById(R.id.imgExperience)
        val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        val ratingTextView: TextView = itemView.findViewById(R.id.tvRating)
        val commentTextView: TextView = itemView.findViewById(R.id.tvComment)
        val usernameTextView: TextView = itemView.findViewById(R.id.etUsername)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_experience, parent, false)
        return ExperienceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        val experience = experienceList[position]
        holder.titleTextView.text = experience.name
        holder.ratingTextView.text = "Puan: ${experience.rating}"
        holder.commentTextView.text = experience.comment

        // Kullanıcı adını almak için DatabaseHelper kullanımı
        val dbHelper = DatabaseHelper(holder.itemView.context)
        val username = dbHelper.getUsernameByUserId(experience.userId)
        holder.usernameTextView.text = username ?: "Bilinmiyor"

        // Fotoğrafı yükleme
        val photoPath = experience.photoPath
        if (!photoPath.isNullOrEmpty()) {
            val file = File(photoPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(photoPath)
                holder.imgExperience.setImageBitmap(bitmap)
            } else {
                holder.imgExperience.setImageResource(R.drawable.placeholder_image) // Yedek resim
            }
        } else {
            holder.imgExperience.setImageResource(R.drawable.placeholder_image) // Yedek resim
        }
    }

    override fun getItemCount(): Int {
        return experienceList.size
    }
}
