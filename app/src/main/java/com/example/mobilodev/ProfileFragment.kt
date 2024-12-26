package com.example.mobilodev

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myapplication.DatabaseHelper

class UpdateProfileFragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var profileImageView: ImageView
    private lateinit var updateButton: Button
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedProfileImage: Bitmap? = null
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameEditText = view.findViewById(R.id.editTextTextPersonName2)
        usernameEditText = view.findViewById(R.id.editTextTextPersonName)
        passwordEditText = view.findViewById(R.id.editTextTextPassword)
        profileImageView = view.findViewById(R.id.profil)
        updateButton = view.findViewById(R.id.GuncelButon)

        // Initialize database helper
        dbHelper = DatabaseHelper(requireContext())

        setupImagePicker()

        profileImageView.setOnClickListener { openImagePicker() }
        updateButton.setOnClickListener { handleUpdateProfile() }
    }

    private fun setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val imageUri = result.data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                profileImageView.setImageBitmap(bitmap)
                selectedProfileImage = bitmap
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun handleUpdateProfile() {
        val name = nameEditText.text.toString().trim()
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
            val user = dbHelper.loginUser(username, password)
            if (user != null) {
                val success = dbHelper.updateUserDetails(
                    userID = user.id,
                    username = username,
                    password = password,
                    name = name,
                    profilePhoto = selectedProfileImage?.let { saveImageToLocal(it) }
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

    private fun saveImageToLocal(bitmap: Bitmap): String {
        // Save the bitmap to local storage and return its path
        val filename = "profile_${System.currentTimeMillis()}.png"
        val fileOutputStream = requireContext().openFileOutput(filename, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
        return requireContext().filesDir.absolutePath + "/" + filename
    }
}
