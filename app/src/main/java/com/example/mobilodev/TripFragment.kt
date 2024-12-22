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
import androidx.navigation.fragment.findNavController
import com.example.myapplication.DatabaseHelper

class TripFragment : Fragment() {

    private lateinit var btnAddExperience: Button
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var etSearch: EditText
    private lateinit var tripListLayout: LinearLayout
    private lateinit var horizontalContainer: LinearLayout
    private lateinit var verticalContainer: LinearLayout
    private lateinit var dbHelper: DatabaseHelper

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
        horizontalContainer = view.findViewById(R.id.horizontalContainer)  // Horizontal container ID
        verticalContainer = view.findViewById(R.id.verticalContainer)  // Vertical container ID

        // DatabaseHelper'ı initialize et
        dbHelper = DatabaseHelper(requireContext())

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

        // VerticalScrollView'a tripleri ekleyin
        verticalContainer.removeAllViews() // Eski verileri temizleyin
        trips.forEach { trip ->
            val tripLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16)
                }
            }

            // HorizontalScrollView içinde resimler
            val horizontalScrollView = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Resimlerin eklenmesi
            trip.images.forEach { imageResId ->
                val imageView = ImageView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                        setMargins(8, 8, 8, 8)
                    }
                    setImageResource(imageResId)  // Veritabanındaki resim kaynağını kullanın
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                horizontalScrollView.addView(imageView)
            }

            // Açıklama TextView
            val descriptionView = TextView(requireContext()).apply {
                id = R.id.tvDescription  // ID ekledik
                text = trip.description // Veritabanındaki açıklamayı kullanın
                textSize = 16f
            }

            // Yorum ve profil fotoğrafı için LinearLayout
            val commentLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(0, 8, 0, 0)  // Margin ayarlama
                this.layoutParams = layoutParams  // LayoutParams'i LinearLayout'a uygulama
            }

            // Profil Fotoğrafı
            val profileImageView = ImageView(requireContext()).apply {
                id = R.id.ivProfileImage  // ID ekledik
                layoutParams = LinearLayout.LayoutParams(50, 50).apply {
                    setMargins(0, 0, 8, 0)
                }
                setImageResource(trip.profileImageResId)  // Profil fotoğrafını veritabanından alın
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            // Yorum metni
            val commentView = TextView(requireContext()).apply {
                id = R.id.tvComment  // ID ekledik
                text = trip.comment // Yorum verisini veritabanından alın
                textSize = 14f
            }

            // Yorum ve profil fotoğrafını layout'a ekle
            commentLayout.addView(profileImageView)
            commentLayout.addView(commentView)

            // Açıklama ve Yorumları Trip Layout'a ekle
            tripLayout.addView(horizontalScrollView)  // Resimlerin olduğu yatay kaydırma ekleniyor
            tripLayout.addView(descriptionView)
            tripLayout.addView(commentLayout)

            // Trip Layout'ını Vertical Container'a ekle
            verticalContainer.addView(tripLayout)
        }
    }

    private fun searchTrips(query: String) {
        // Arama işlemi: Veritabanından filtrelenmiş sonuçları getir
        val filteredTrips = dbHelper.searchTrips(query)  // Arama sonuçlarını al

        // Ekranı temizle ve yeniden yükle
        verticalContainer.removeAllViews()

        filteredTrips.forEach { trip ->
            val tripLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16)
                }
            }

            // HorizontalScrollView içinde resimler
            val horizontalScrollView = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Resimlerin eklenmesi
            trip.images.forEach { imageResId ->
                val imageView = ImageView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                        setMargins(8, 8, 8, 8)
                    }
                    setImageResource(imageResId)  // Arama sonucu trip'in resmini ekleyin
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                horizontalScrollView.addView(imageView)
            }

            // Açıklama TextView
            val descriptionView = TextView(requireContext()).apply {
                text = trip.description // Arama sonucu trip'in açıklamasını ekleyin
                textSize = 16f
            }

            // Yorum metni
            val commentView = TextView(requireContext()).apply {
                text = "Yorum: Çok güzel bir yer!" // Yorum kısmını ekleyin
                textSize = 14f
            }

            // Açıklama ve Yorumları Vertical Layout'a ekleyin
            tripLayout.addView(horizontalScrollView)  // Resimleri yatay kaydırma olarak ekleyin
            tripLayout.addView(descriptionView)
            tripLayout.addView(commentView)

            // Vertical Container'a trip'i ekleyin
            verticalContainer.addView(tripLayout)
        }
    }
}







