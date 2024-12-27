package com.example.mobilodev

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DatabaseHelper
import com.example.myapplication.Place

class MainFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var experienceAdapter1: ExperienceAdapter
    private lateinit var experienceAdapter2: ExperienceAdapter
    private lateinit var experienceAdapter3: ExperienceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment layout dosyasını bağla
        val view = inflater.inflate(R.layout.activity_main, container, false)

        // Butonları bağla ve tıklama işlemleri
        val buttonLogin = view.findViewById<Button>(R.id.btnLogin)
        buttonLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .addToBackStack(null)
                .commit()
        }

        val buttonSignUp = view.findViewById<Button>(R.id.btnSignUp)
        buttonSignUp.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignUpFragment())
                .addToBackStack(null)
                .commit()
        }

        val buttonAddExperience = view.findViewById<Button>(R.id.btnAddExperience)
        buttonAddExperience.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .addToBackStack(null)
                .commit()
        }

        val buttonAddExperience2 = view.findViewById<Button>(R.id.btnAddExperience2)
        buttonAddExperience2.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TripFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DatabaseHelper başlatma
        databaseHelper = DatabaseHelper(requireContext())

        // RecyclerView'ları ayarla
        setupRecyclerView1(view)
        setupRecyclerView2(view)
        setupRecyclerView3(view)
    }

    private fun setupRecyclerView1(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView1)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        experienceAdapter1 = ExperienceAdapter(getAllTrips())
        recyclerView?.adapter = experienceAdapter1
    }

    private fun setupRecyclerView2(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView2)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        experienceAdapter2 = ExperienceAdapter(getTopRatedTrips())
        recyclerView?.adapter = experienceAdapter2
    }

    private fun setupRecyclerView3(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView3)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        experienceAdapter3 = ExperienceAdapter(getUserTrips())
        recyclerView?.adapter = experienceAdapter3
    }

    private fun getAllTrips(): List<Place> {
        return databaseHelper.getAllTrips()
    }

    private fun getTopRatedTrips(): List<Place> {
        return databaseHelper.getTopRatedTrips()
    }

    private fun getUserTrips(): List<Place> {
        return databaseHelper.getUserTrips()
    }
}
