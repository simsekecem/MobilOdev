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

    // Kullanılacak UI bileşenlerini tanımlıyoruz
    private lateinit var etPlaceName: EditText // Seyahat adı girişi
    private lateinit var etPlaceRate: EditText // Puan girişi
    private lateinit var etReview: EditText // Yorum girişi
    private lateinit var imgPhoto1: ImageView // Fotoğraf göstermek için ImageView
    private lateinit var btnSave: Button // Kaydet butonu
    private lateinit var tvUsername: TextView // Kullanıcı adı gösterimi
    private lateinit var databaseHelper: DatabaseHelper // Veritabanı işlemleri için yardımcı sınıf
    private var photoUris: MutableList<Uri> = mutableListOf() // Fotoğraf URI'lerini tutar

    private val PICK_IMAGE_REQUEST = 1 // Fotoğraf seçme isteği için bir sabit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Fragment ile ilgili layout'u bağlar
        val view = inflater.inflate(R.layout.activity_command, container, false)

        // Layout'taki view'ları Java/Kotlin nesnelerine bağlar
        tvUsername = view.findViewById(R.id.tvUsername)
        etPlaceName = view.findViewById(R.id.etPlaceName)
        etPlaceRate = view.findViewById(R.id.etPlaceRate)
        etReview = view.findViewById(R.id.etReview)
        imgPhoto1 = view.findViewById(R.id.imgPhoto1)
        btnSave = view.findViewById(R.id.btnSave)

        // Veritabanı yardımcı sınıfını başlatır
        databaseHelper = DatabaseHelper(requireContext())

        // Kullanıcı adını veritabanından çekip TextView'e yazdırır
        tvUsername.text = "Kullanıcı Adı: ${getUsername()}"

        // Fotoğraf eklemek için tıklama dinleyicisi ekler
        imgPhoto1.setOnClickListener { openImagePicker() }

        // Kaydet butonuna tıklama işlemini tanımlar
        btnSave.setOnClickListener {
            val placeName = etPlaceName.text.toString() // Kullanıcıdan alınan seyahat adı
            val placeRate = etPlaceRate.text.toString() // Kullanıcıdan alınan puan
            val review = etReview.text.toString() // Kullanıcıdan alınan yorum

            // Fotoğrafları byte array'e dönüştürür
            val photoByteArrays = mutableListOf<ByteArray>()
            for (uri in photoUris) {
                val photoBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                photoByteArrays.add(convertBitmapToByteArray(photoBitmap))
            }

            // Veritabanına kaydeder
            databaseHelper.insertPlace(getUsername(), placeName, placeRate, review, photoByteArrays)

            // Kullanıcıya işlem başarılı mesajı gösterir
            Toast.makeText(requireContext(), "Veri Kaydedildi", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    /**
     * Veritabanından kullanıcı adını çeker.
     * Bu yöntem veritabanı veya başka bir veri kaynağından kullanıcı adı almak için düzenlenebilir.
     */
    private fun getUsername(): String {
        return databaseHelper.getCurrentUsername() ?: "Unknown User" // Eğer kullanıcı adı yoksa varsayılan bir değer döner
    }

    /**
     * Fotoğraf seçme işlemini başlatır.
     */
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            // Birden fazla fotoğraf seçilmesine izin ver (Android 14+ için)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    /**
     * Fotoğraf seçme sonucunu işler.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.clipData != null) {
                // Eğer birden fazla fotoğraf seçildiyse
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val photoUri = data.clipData!!.getItemAt(i).uri
                    photoUris.add(photoUri)

                    // Fotoğrafı ImageView'de göster
                    if (i == 0) imgPhoto1.setImageURI(photoUri)
                }
            } else {
                // Eğer sadece bir fotoğraf seçildiyse
                val photoUri = data.data
                photoUris.add(photoUri!!)
                imgPhoto1.setImageURI(photoUri)
            }
        }
    }

    /**
     * Bitmap'i byte array'e dönüştürür.
     */
    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
