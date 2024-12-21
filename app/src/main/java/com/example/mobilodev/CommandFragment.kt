package com.example.mobilodev

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myapplication.DatabaseHelper
import java.io.ByteArrayOutputStream
import android.os.Build

class TravelFragment : Fragment() {

    private lateinit var etPlaceName: EditText
    private lateinit var etPlaceFeatures: EditText
    private lateinit var etReview: EditText
    private lateinit var etVisitDate: EditText
    private lateinit var imgPhoto1: ImageView
    private lateinit var imgPhoto2: ImageView
    private lateinit var imgPhoto3: ImageView
    private lateinit var imgPhoto4: ImageView
    private lateinit var btnSave: Button
    private lateinit var tvUsername: TextView
    private lateinit var databaseHelper: DatabaseHelper
    private var photoUris: MutableList<Uri> = mutableListOf()

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Layout'u bağlama
        val view = inflater.inflate(R.layout.activity_command, container, false)

        // View'ları bağlama
        tvUsername = view.findViewById(R.id.tvUsername)
        etPlaceName = view.findViewById(R.id.etPlaceName)
        etPlaceFeatures = view.findViewById(R.id.etPlaceFeatures)
        etReview = view.findViewById(R.id.etReview)
        etVisitDate = view.findViewById(R.id.etVisitDate)
        imgPhoto1 = view.findViewById(R.id.imgPhoto1)
        imgPhoto2 = view.findViewById(R.id.imgPhoto2)
        imgPhoto3 = view.findViewById(R.id.imgPhoto3)
        imgPhoto4 = view.findViewById(R.id.imgPhoto4)
        btnSave = view.findViewById(R.id.btnSave)

        databaseHelper = DatabaseHelper(requireContext())

        // Kullanıcı adı gösterimi
        tvUsername.text = "Kullanıcı Adı: ${getUsername()}"

        // Fotoğraf seçimi
        imgPhoto1.setOnClickListener { openImagePicker() }
        imgPhoto2.setOnClickListener { openImagePicker() }
        imgPhoto3.setOnClickListener { openImagePicker() }
        imgPhoto4.setOnClickListener { openImagePicker() }

        // Kaydet Butonu
        btnSave.setOnClickListener {
            val placeName = etPlaceName.text.toString()
            val placeFeatures = etPlaceFeatures.text.toString()
            val review = etReview.text.toString()
            val visitDate = etVisitDate.text.toString()

            // Fotoğrafları byte dizilerine dönüştürme
            val photoByteArrays = mutableListOf<ByteArray>()
            for (uri in photoUris) {
                val photoBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                photoByteArrays.add(convertBitmapToByteArray(photoBitmap))
            }

            // Veritabanına kaydetme
            databaseHelper.insertPlace(getUsername(), placeName, placeFeatures, review, visitDate, photoByteArrays)

            Toast.makeText(requireContext(), "Veri Kaydedildi", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun getUsername(): String {
        return "User123" // Gerçek kullanıcı adı için veritabanı veya SharedPreferences kullanılabilir
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            // Birden fazla fotoğraf seçilmesine izin ver
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.clipData != null) {
                // Eğer birden fazla fotoğraf seçildiyse
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val photoUri = data.clipData!!.getItemAt(i).uri
                    photoUris.add(photoUri)

                    // Fotoğrafı ImageView'lerde gösterelim
                    when (i) {
                        0 -> imgPhoto1.setImageURI(photoUri)
                        1 -> imgPhoto2.setImageURI(photoUri)
                        2 -> imgPhoto3.setImageURI(photoUri)
                        3 -> imgPhoto4.setImageURI(photoUri)
                    }
                }
            } else {
                // Eğer sadece bir fotoğraf seçildiyse
                val photoUri = data.data
                photoUris.add(photoUri!!)
                imgPhoto1.setImageURI(photoUri)
            }
        }
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}






