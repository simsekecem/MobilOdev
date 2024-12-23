package com.example.mobilodev

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiloodev.ExperienceAdapter
import com.example.myapplication.DatabaseHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiloodev.Experience

class UserFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var experienceAdapter1: ExperienceAdapter
    private lateinit var experienceAdapter2: ExperienceAdapter
    private lateinit var experienceAdapter3: ExperienceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the activity_trip layout instead of using ViewBinding
        return inflater.inflate(R.layout.activity_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DatabaseHelper'ı başlat
        databaseHelper = DatabaseHelper(requireContext())

        // RecyclerView'lar için layout manager ve adapter ayarları
        setupRecyclerView1(view)
        setupRecyclerView2(view)
        setupRecyclerView3(view)
    }

    private fun setupRecyclerView1(view: View) {
        // "İlginizi Çekebilir" RecyclerView
        val recyclerView2 = view.findViewById<RecyclerView>(R.id.recyclerView2)
        recyclerView2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        experienceAdapter1 = ExperienceAdapter(getAllTrips())
        recyclerView2.adapter = experienceAdapter1
    }

    private fun setupRecyclerView2(view: View) {
        // "En İyiler" RecyclerView (Rating'e göre sıralama)
        val recyclerView3 = view.findViewById<RecyclerView>(R.id.recyclerView3)
        recyclerView3.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        experienceAdapter2 = ExperienceAdapter(getTopRatedTrips())
        recyclerView3.adapter = experienceAdapter2
    }

    private fun setupRecyclerView3(view: View) {
        // "Deneyimlerim" RecyclerView (Kullanıcıya ait veriler)
        val recyclerView4 = view.findViewById<RecyclerView>(R.id.recyclerView4)
        recyclerView4.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        experienceAdapter3 = ExperienceAdapter(getUserTrips())
        recyclerView4.adapter = experienceAdapter3
    }

    // Database'den tüm gezileri getir
    private fun getAllTrips(): List<Experience> {
        return databaseHelper.getAllTrips() // Tüm veriyi al
    }

    // Database'den en yüksek puanlı gezileri getir
    private fun getTopRatedTrips(): List<Experience> {
        return databaseHelper.getTopRatedTrips() // Rating'e göre sıralanmış veriyi al
    }

    // Database'den kullanıcının gezilerini getir
    private fun getUserTrips(): List<Experience> {
        return databaseHelper.getUserTrips() // Kullanıcıya ait veriyi al
    }
}

