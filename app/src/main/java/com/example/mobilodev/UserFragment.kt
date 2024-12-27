package com.example.mobilodev

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.DatabaseHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Place

class UserFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var experienceAdapter1: ExperienceAdapter
    private lateinit var experienceAdapter2: ExperienceAdapter
    private lateinit var experienceAdapter3: ExperienceAdapter
    private lateinit var btnLogout: Button
    private lateinit var btnComment: Button
    private lateinit var btnProfile: Button
    private lateinit var btnSeeAll: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using the activity_trip layout instead of using ViewBinding
        return inflater.inflate(R.layout.activity_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DatabaseHelper'ı başlat
        databaseHelper = DatabaseHelper(requireContext())

        // RecyclerView'lar için layout manager ve adapter ayarları
        setupRecyclerView1(view)
        setupRecyclerView2(view)
        setupRecyclerView3(view)

        btnLogout.setOnClickListener {
            databaseHelper.setCurrentUser(null)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment())
                .addToBackStack(null)
                .commit()
        }
        btnComment.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CommandFragment())
                .addToBackStack(null)
                .commit()
        }
        btnProfile.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
        btnSeeAll.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TripFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerView1(view: View) {
        // "İlginizi Çekebilir" RecyclerView
        val recyclerView2 = view.findViewById<RecyclerView>(R.id.recyclerView1)
        recyclerView2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        experienceAdapter1 = ExperienceAdapter(getAllTrips())
        recyclerView2.adapter = experienceAdapter1
    }

    private fun setupRecyclerView2(view: View) {
        // "En İyiler" RecyclerView (Rating'e göre sıralama)
        val recyclerView3 = view.findViewById<RecyclerView>(R.id.recyclerView2)
        recyclerView3.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        experienceAdapter2 = ExperienceAdapter(getTopRatedTrips())
        recyclerView3.adapter = experienceAdapter2
    }

    private fun setupRecyclerView3(view: View) {
        // "Deneyimlerim" RecyclerView (Kullanıcıya ait veriler)
        val recyclerView4 = view.findViewById<RecyclerView>(R.id.recyclerView3)
        recyclerView4.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        experienceAdapter3 = ExperienceAdapter(getUserTrips())
        recyclerView4.adapter = experienceAdapter3
    }

    // Database'den tüm gezileri getir
    private fun getAllTrips(): List<Place> {
        return databaseHelper.getAllTrips() // Tüm veriyi al
    }

    // Database'den en yüksek puanlı gezileri getir
    private fun getTopRatedTrips(): List<Place> {
        return databaseHelper.getTopRatedTrips() // Rating'e göre sıralanmış veriyi al
    }

    // Database'den kullanıcının gezilerini getir
    private fun getUserTrips(): List<Place> {
        return databaseHelper.getUserTrips() // Kullanıcıya ait veriyi al
    }
}

