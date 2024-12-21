package com.example.mobilproje

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mobilodev.R
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.ImageView
import androidx.navigation.fragment.findNavController //butonlar için
import com.example.myapplication.DatabaseHelper

class TripFragment : Fragment() {

    private lateinit var btnAddExperience: Button
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var etSearch: EditText
    private lateinit var tripListLayout: LinearLayout
    private lateinit var horizontalContainer: LinearLayout
    private lateinit var verticalContainer: LinearLayout
    private lateinit var dbHelper: DatabaseHelper // SQLite DatabaseHelper eklenecek

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View'leri bağla
        btnAddExperience = view.findViewById(R.id.btnAddExperience)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnSignUp = view.findViewById(R.id.btnSignUp)
        etSearch = view.findViewById(R.id.etSearch)
        tripListLayout = view.findViewById(R.id.tripListLayout)

        // DatabaseHelper'ı initialize et
        dbHelper = DatabaseHelper(requireContext())

        // Butonlar için tıklama olayları
// btnAddExperience.setOnClickListener {
//     findNavController().navigate(R.id.action_TripFragment_to_CommentFragment)
// }

// btnLogin.setOnClickListener {
//     findNavController().navigate(R.id.action_TripFragment_to_LoginFragment)
// }

// btnSignUp.setOnClickListener {
//     findNavController().navigate(R.id.action_tripFragment_to_signUpFragment)
// }

        // Veritabanından verileri yükle
        loadTrips()

        // Arama kutusuna metin yazıldığında tetiklenecek listener
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // Burada önceki metin değişikliği işlemleri yapılabilir
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Metin değiştiğinde arama işlemi yapılır
                searchTrips(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable?) {
                // Metin değiştikten sonra yapılacak işlem (isteğe bağlı)
            }
        })
    }

    private fun loadTrips() {
        // Veritabanından tüm tripleri al
        val trips = dbHelper.getAllTrips()

        // HorizontalScrollView'a fotoğrafları ekle
        horizontalContainer.removeAllViews() // Eski verileri temizle
        trips.forEach { trip: Trip ->  // Trip türünü belirttik
            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                    setMargins(8, 8, 8, 8)
                }
                setImageResource(trip.imageResId) // Veritabanındaki resim kaynağını kullan
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            horizontalContainer.addView(imageView)
        }

        // VerticalScrollView'a açıklama ve yorumları ekle
        verticalContainer.removeAllViews() // Eski verileri temizle
        trips.forEach { trip: Trip ->  // Trip türünü belirttik
            val tripLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16)
                }
            }

            val descriptionView = TextView(requireContext()).apply {
                text = trip.description // Veritabanındaki açıklama
                textSize = 16f
            }

            val commentView = TextView(requireContext()).apply {
                text = "Yorum: Çok güzel bir yer!" // Yorum ekleyebilirsiniz
                textSize = 14f
            }

            tripLayout.addView(descriptionView)
            tripLayout.addView(commentView)
            verticalContainer.addView(tripLayout)
        }
    }

    private fun searchTrips(query: String) {
        // Arama işlemi: Veritabanından filtrelenmiş sonuçları getir
        val filteredTrips: List<Trip> = dbHelper.searchTrips(query)  // Türü belirttik

        // Ekranı temizle ve yeniden yükle
        horizontalContainer.removeAllViews()
        verticalContainer.removeAllViews()

        filteredTrips.forEach { trip: Trip ->  // Türü belirttik
            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                    setMargins(8, 8, 8, 8)
                }
                setImageResource(trip.imageResId) // Arama sonucu trip'in resmini ekle
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            horizontalContainer.addView(imageView)

            val tripLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16)
                }
            }

            val descriptionView = TextView(requireContext()).apply {
                text = trip.description // Arama sonucu trip'in açıklamasını ekle
                textSize = 16f
            }

            val commentView = TextView(requireContext()).apply {
                text = "Yorum: Çok güzel bir yer!" // Yorum kısmını ekleyebilirsiniz
                textSize = 14f
            }

            tripLayout.addView(descriptionView)
            tripLayout.addView(commentView)
            verticalContainer.addView(tripLayout)
        }
    }

}





