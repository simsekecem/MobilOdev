package com.example.mobilodev

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.DatabaseHelper
import java.io.File
import java.io.FileOutputStream
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

    private lateinit var photoPickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoPickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedPhotoUri = result.data?.data
                imgPhoto1.setImageURI(selectedPhotoUri)
            }
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                openImagePicker()
            } else {
                Toast.makeText(
                    requireContext(),
                    "İzin verilmedi, fotoğraf seçemezsiniz!",
                    Toast.LENGTH_SHORT
                ).show()
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

        val currentUser = databaseHelper.getUserDetails()
        tvUsername.text = "Kullanıcı Adı: ${currentUser?.username ?: "Bilinmiyor"}"

        imgPhoto1.setOnClickListener { checkAndRequestPermission() }
        btnSave.setOnClickListener { handleSavePlace() }

        return view
    }

    private fun handleSavePlace() {
        val placeName = etPlaceName.text.toString().trim()
        val placeRate = etPlaceRate.text.toString().toIntOrNull() ?: 0
        val review = etReview.text.toString().trim()

        if (placeName.isEmpty()) {
            Toast.makeText(requireContext(), "Yer adı boş olamaz!", Toast.LENGTH_SHORT).show()
            return
        }

        val photoPath = selectedPhotoUri?.let { saveImageToDrawable(it) }
        if (photoPath != null) {
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

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        photoPickerLauncher.launch(intent)
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun saveImageToDrawable(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val drawableFile = File(requireContext().filesDir, "drawable")
            if (!drawableFile.exists()) drawableFile.mkdir()
            val fileName = "IMG_${System.currentTimeMillis()}.jpg"
            val file = File(drawableFile, fileName)

            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
