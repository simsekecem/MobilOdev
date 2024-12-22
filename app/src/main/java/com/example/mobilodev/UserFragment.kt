package com.example.mobillibrary

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mobilodev.R
import com.example.myapplication.DatabaseHelper

class UserFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var bestTripsLayout: LinearLayout
    private lateinit var scrollViewAllTrips: LinearLayout
    private lateinit var experienceLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_user, container, false)

        // Veritabanı başlatma
        dbHelper = DatabaseHelper(requireContext())

        // Layoutlar
        bestTripsLayout = view.findViewById(R.id.bestTripsLayout)
        scrollViewAllTrips = view.findViewById(R.id.scrollViewAllTrips)
        experienceLayout = view.findViewById(R.id.experienceLayout)

        // Butonlar ve arama çubuğu
        val btnProfile = view.findViewById<Button>(R.id.btnProfile)
        val btnComment = view.findViewById<Button>(R.id.btnComment)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnSeeAll = view.findViewById<Button>(R.id.btnAddExperience2)
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val btnSearch = view.findViewById<Button>(R.id.btnSearch)

        // Butonlar için tıklama olayları

        /*
        // "Hesap Sayfasına Git" butonuna tıklama
        // Bu buton tıklandığında kullanıcının profil sayfasına yönlendirilmesi sağlanır.
        btnProfile.setOnClickListener {
            navigateToProfile()
        }

        // "Yorum Yap" butonuna tıklama
        // Bu buton tıklandığında kullanıcının yorum ekleme sayfasına yönlendirilmesi sağlanır.
        btnComment.setOnClickListener {
            navigateToComment()
        }

        // "Çıkış Yap" butonuna tıklama
        // Bu buton tıklandığında kullanıcı uygulamadan çıkış yapar.
        btnLogout.setOnClickListener {
            logout()
        }

        // "Tüm Gezileri Gör" butonuna tıklama
        // Bu buton tıklandığında veritabanındaki tüm geziler gösterilir.
        btnSeeAll.setOnClickListener {
            showAllTrips()
        }
        */

        // Deneyim ekleme butonunun tıklama işlemi
// val btnAddExperience = view.findViewById<Button>(R.id.btnAddExperience2)
// btnAddExperience.setOnClickListener {
//     // Burada, kullanıcıyı deneyim ekleme sayfasına yönlendireceğiz.
//     val intent = Intent(requireContext(), CommandActivity::class.java)
//     startActivity(intent)
// }

        // "Arama Yap" butonuna tıklama
        // Bu buton tıklandığında arama çubuğuna yazılan sorgu kullanılarak geziler aratılır.
        btnSearch.setOnClickListener {
            val searchQuery = searchEditText.text.toString().trim()
            searchTrips(searchQuery)
        }

        // En iyi gezileri yükle
        showBestTrips()
        showUserExperiences()
        showAllTrips()

        return view
    }

    // Profil sayfasına gitmek için kullanılan fonksiyon
// private fun navigateToProfile() {
//     val intent = Intent(requireContext(), ProfileActivity::class.java)
//     startActivity(intent)
//     Toast.makeText(context, "Hesap sayfasına gidiliyor...", Toast.LENGTH_SHORT).show()
// }

// Yorum ekleme sayfasına gitmek için kullanılan fonksiyon
// private fun navigateToComment() {
//     val intent = Intent(requireContext(), CommentActivity::class.java)
//     startActivity(intent)
//     Toast.makeText(context, "Yorum ekleme sayfasına gidiliyor...", Toast.LENGTH_SHORT).show()
// }

// Kullanıcı çıkışı yapar
// private fun logout() {
//     Toast.makeText(context, "Çıkış yapılıyor...", Toast.LENGTH_SHORT).show()
//     activity?.finish() // Uygulama kapanacak
// }

    // Tüm gezileri gösteren fonksiyon
    private fun showAllTrips() {
        val cursor: Cursor = dbHelper.getTripsWithCommentsAndUserProfile()
        scrollViewAllTrips.removeAllViews() // Listeyi temizle

        if (cursor.moveToFirst()) {
            do {
                val tripName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val tripDescription = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
                val tripRating = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING))
                val tripImage = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE))
                val userComment = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMMENT))
                val userProfilePic = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_PIC))

                val tripView = LayoutInflater.from(context).inflate(R.layout.trip_item, scrollViewAllTrips, false)
                val tripImageView: ImageView = tripView.findViewById(R.id.tripImageView)
                val tripNameView: TextView = tripView.findViewById(R.id.tripNameView)
                val tripDescriptionView: TextView = tripView.findViewById(R.id.tripDescriptionView)
                val tripRatingView: TextView = tripView.findViewById(R.id.tripRatingView)
                val userCommentView: TextView = tripView.findViewById(R.id.userCommentView) // Yorum alanı
                val userProfilePicView: ImageView = tripView.findViewById(R.id.userProfilePicView) // Profil fotoğrafı alanı

                // Verileri UI bileşenlerine bağla
                tripNameView.text = tripName
                tripDescriptionView.text = tripDescription
                tripRatingView.text = "Puan: $tripRating"
                userCommentView.text = "Yorum: $userComment"
                // Profil fotoğrafını yükle (Varsa)
                if (userProfilePic.isNotEmpty()) {
                    // Eğer profil fotoğrafı varsa, burada resim yükleyebilirsiniz
                    userProfilePicView.setImageURI(Uri.parse(userProfilePic)) // Ya da Picasso gibi bir kütüphane kullanabilirsiniz
                } else {
                    userProfilePicView.setImageResource(R.drawable.placeholder_image) // Varsayılan resim
                }

                tripImageView.setImageResource(R.drawable.placeholder_image) // Örnek resim

                scrollViewAllTrips.addView(tripView)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(context, "Hiç gezi bulunamadı.", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }

    // En yüksek puanlı gezileri gösteren fonksiyon
    private fun showBestTrips() {
        val cursor: Cursor = dbHelper.getTripsWithCommentsAndUserProfile()
        bestTripsLayout.removeAllViews() // Listeyi temizle

        if (cursor.moveToFirst()) {
            do {
                val tripName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val tripDescription = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
                val tripRating = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING))
                val tripImage = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE))
                val userComment = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMMENT))
                val userProfilePic = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_PIC))

                val tripView = LayoutInflater.from(context).inflate(R.layout.trip_item, bestTripsLayout, false)
                val tripImageView: ImageView = tripView.findViewById(R.id.tripImageView)
                val tripNameView: TextView = tripView.findViewById(R.id.tripNameView)
                val tripDescriptionView: TextView = tripView.findViewById(R.id.tripDescriptionView)
                val tripRatingView: TextView = tripView.findViewById(R.id.tripRatingView)
                val userCommentView: TextView = tripView.findViewById(R.id.userCommentView) // Yorum alanı
                val userProfilePicView: ImageView = tripView.findViewById(R.id.userProfilePicView) // Profil fotoğrafı alanı

                // Verileri UI bileşenlerine bağla
                tripNameView.text = tripName
                tripDescriptionView.text = tripDescription
                tripRatingView.text = "Puan: $tripRating"
                userCommentView.text = "Yorum: $userComment"
                // Profil fotoğrafını yükle (Varsa)
                if (userProfilePic.isNotEmpty()) {
                    userProfilePicView.setImageURI(Uri.parse(userProfilePic)) // Ya da Picasso gibi bir kütüphane kullanabilirsiniz
                } else {
                    userProfilePicView.setImageResource(R.drawable.placeholder_image) // Varsayılan resim
                }

                tripImageView.setImageResource(R.drawable.placeholder_image) // Örnek resim

                bestTripsLayout.addView(tripView)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(context, "Hiç gezi bulunamadı.", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }

    private fun searchTrips(query: String) {
        // Arama işlemi burada yapılacak
        val cursor: Cursor = dbHelper.searchTrips(query)
        scrollViewAllTrips.removeAllViews() // Listeyi temizle

        if (cursor.moveToFirst()) {
            do {
                val tripName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val tripDescription = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
                val tripRating = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING))
                val tripImage = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE))

                val tripView = LayoutInflater.from(context).inflate(R.layout.trip_item, scrollViewAllTrips, false)
                val tripImageView: ImageView = tripView.findViewById(R.id.tripImageView)
                val tripNameView: TextView = tripView.findViewById(R.id.tripNameView)
                val tripDescriptionView: TextView = tripView.findViewById(R.id.tripDescriptionView)
                val tripRatingView: TextView = tripView.findViewById(R.id.tripRatingView)

                // Verileri UI bileşenlerine bağla
                tripNameView.text = tripName
                tripDescriptionView.text = tripDescription
                tripRatingView.text = "Puan: $tripRating"
                tripImageView.setImageResource(R.drawable.placeholder_image) // Örnek resim

                scrollViewAllTrips.addView(tripView)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(context, "Aramanıza uygun gezi bulunamadı.", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }

    // Kullanıcıya ait deneyimleri görüntüleyen fonksiyon
    private fun showUserExperiences() {
        val cursor: Cursor = dbHelper.getUserExperiences() // Bu fonksiyon, kullanıcının gezilerini döndürecek
        experienceLayout.removeAllViews() // Mevcut deneyimleri temizle

        if (cursor.moveToFirst()) {
            do {
                val tripName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val tripDescription = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
                val tripRating = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATING))
                val tripImage = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE))
                val userComment = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COMMENT))
                val userProfileImage = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PROFILE_IMAGE))

                val tripView = LayoutInflater.from(context).inflate(R.layout.trip_item, experienceLayout, false)
                val tripImageView: ImageView = tripView.findViewById(R.id.tripImageView)
                val tripNameView: TextView = tripView.findViewById(R.id.tripNameView)
                val tripDescriptionView: TextView = tripView.findViewById(R.id.tripDescriptionView)
                val tripRatingView: TextView = tripView.findViewById(R.id.tripRatingView)
                val userCommentView: TextView = tripView.findViewById(R.id.userCommentView)
                val userProfileImageView: ImageView = tripView.findViewById(R.id.userProfileImageView)

                // Verileri UI bileşenlerine bağla
                tripNameView.text = tripName
                tripDescriptionView.text = tripDescription
                tripRatingView.text = "Puan: $tripRating"
                userCommentView.text = userComment
                userProfileImageView.setImageResource(R.drawable.placeholder_profile_image) // Burada userProfileImage'ı set et

                // Trip resmi
                tripImageView.setImageResource(R.drawable.placeholder_image) // Burada tripImage'ı set et

                experienceLayout.addView(tripView)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(context, "Hiç gezi bulunamadı.", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }
}

