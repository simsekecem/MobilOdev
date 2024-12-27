package com.example.mobilodev

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.DatabaseHelper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ProfileFragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var profileImageView: ImageView
    private lateinit var updateButton: Button
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedProfileImageUri: Uri? = null
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = view.findViewById(R.id.nameEditText)
        usernameEditText = view.findViewById(R.id.usernameEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        profileImageView = view.findViewById(R.id.profileImageView)
        updateButton = view.findViewById(R.id.updateButton)

        dbHelper = DatabaseHelper(requireContext())

        setupImagePicker()
        setupPermissionLauncher()
        loadUserProfile()

        profileImageView.setOnClickListener { checkAndRequestPermission() }

        updateButton.setOnClickListener {
            handleUpdateProfile()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    val drawablePath = saveImageToDrawable(imageUri)
                    if (drawablePath != null) {
                        profileImageView.setImageURI(Uri.parse(drawablePath))
                        selectedProfileImageUri = Uri.parse(drawablePath)
                    } else {
                        Toast.makeText(requireContext(), "Resim kaydedilemedi!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupPermissionLauncher() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openImagePicker()
            } else {
                Toast.makeText(requireContext(), "İzin verilmedi, fotoğraf seçemezsiniz!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun loadUserProfile() {
        val user = dbHelper.getUserDetails()
        if (user != null) {
            nameEditText.setText(user.name ?: "")
            usernameEditText.setText(user.username)
            passwordEditText.setText(user.password)

            // Veritabanından gelen profil fotoğrafı yolunu kullan
            user.profilePhoto?.let {
                val drawablePath = it
                val file = File(drawablePath)
                if (file.exists()) {
                    profileImageView.setImageURI(Uri.fromFile(file))
                } else {
                    Toast.makeText(requireContext(), "Profil resmi bulunamadı.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Kullanıcı bilgileri yüklenemedi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToDrawable(imageUri: Uri): String? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val drawableFile = File(requireContext().filesDir, "drawable")
            if (!drawableFile.exists()) drawableFile.mkdir()
            val file = File(drawableFile, "profile_image.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            Log.e("ProfileFragment", "saveImageToDrawable: ${e.message}")
            null
        }
    }

    private fun handleUpdateProfile() {
        val name = nameEditText.text.toString().trim()
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
            val user = dbHelper.loginUser(username, password)
            if (user != null) {
                val success = dbHelper.updateUserDetails(
                    username = username,
                    password = password,
                    name = name,
                    profilePhoto = selectedProfileImageUri?.toString()
                )
                if (success) {
                    Toast.makeText(requireContext(), "Profil güncellendi", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Güncelleme başarısız", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
        }
    }
}
