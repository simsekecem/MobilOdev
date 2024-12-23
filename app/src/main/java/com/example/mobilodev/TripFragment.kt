package com.example.mobilproje

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilodev.R
import com.example.mobiloodev.ExperienceAdapter
import com.example.myapplication.DatabaseHelper

class TripFragment : Fragment() {

    private lateinit var btnAddExperience: Button
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tripAdapter: ExperienceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View'leri bağla
        btnAddExperience = view.findViewById(R.id.btnAddExperience)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnSignUp = view.findViewById(R.id.btnSignUp)
        etSearch = view.findViewById(R.id.searchEditText)  // Arama alanını bağla
        recyclerView = view.findViewById(R.id.recyclerView)  // RecyclerView'u bağla

        // DatabaseHelper'ı initialize et
        dbHelper = DatabaseHelper(requireContext())

        // RecyclerView ayarları
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        tripAdapter = ExperienceAdapter(emptyList())  // Başlangıçta boş liste
        recyclerView.adapter = tripAdapter

        // Veritabanından verileri yükle
        loadTrips()

        // Arama kutusuna metin yazıldığında tetiklenecek listener
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                searchTrips(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun loadTrips() {
        // Veritabanından tüm tripleri alınır
        val trips = dbHelper.getAllTrips()

        // RecyclerView adapter'ına verileri ekleyin
        tripAdapter = ExperienceAdapter(trips)
        recyclerView.adapter = tripAdapter
    }

    private fun searchTrips(query: String) {
        // Arama işlemi: Veritabanından filtrelenmiş sonuçları getir
        val filteredTrips = dbHelper.searchTrips(query)

        // RecyclerView adapter'ına filtrelenmiş verileri ekleyin
        tripAdapter = ExperienceAdapter(filteredTrips)
        recyclerView.adapter = tripAdapter
    }
}
