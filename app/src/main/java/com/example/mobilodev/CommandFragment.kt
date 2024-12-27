package com.example.mobilodev

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myapplication.DatabaseHelper
import android.app.Activity


class CommandFragment : Fragment() {

    private lateinit var etPlaceName: EditText
    private lateinit var etPlaceRate: EditText
    private lateinit var etReview: EditText
    private lateinit var imgPhoto1: ImageView
    private lateinit var btnSave: Button
    private lateinit var tvUsername: TextView
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedPhotoUri: Uri? = null

    // Fotoğraf seçmek için Activity Result Launcher
    private lateinit var photoPickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fotoğraf seçme işlemini başlatan launcher'ı tanımlıyoruz
        photoPickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedPhotoUri = result.data?.data
                imgPhoto1.setImageURI(selectedPhotoUri)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_command, container, false)

        tvUsername = view.findViewById(R.id.tvUsername)
        etPlaceName = view.findViewById(R.id.etPlaceName)
        etPlaceRate = view.findViewById(R.id.etPlaceRate)
        etReview = view.findViewById(R.id.etReview)
        imgPhoto1 = view.findViewById(R.id.imgPhoto1)
        btnSave = view.findViewById(R.id.btnSave)

        databaseHelper = DatabaseHelper(requireContext())

        tvUsername.text = "Kullanıcı Adı: ${getUsername()}"

        imgPhoto1.setOnClickListener { openImagePicker() }

        btnSave.setOnClickListener {
            val placeName = etPlaceName.text.toString()
            val placeRate = etPlaceRate.text.toString().toIntOrNull() ?: 0
            val review = etReview.text.toString()

            if (selectedPhotoUri != null) {
                val photoPath = selectedPhotoUri.toString()
                val isInserted = databaseHelper.insertPlace(placeName, review, photoPath, placeRate)
                if (isInserted) {
                    Toast.makeText(requireContext(), "Veri Kaydedildi", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Hata oluştu!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen bir fotoğraf seçin!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun getUsername(): String {
        return DatabaseHelper.currentUsername ?: "Unknown User"
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        photoPickerLauncher.launch(intent)
    }
}
