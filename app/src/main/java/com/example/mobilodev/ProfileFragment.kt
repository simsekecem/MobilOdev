package com.example.mobilodev

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.DatabaseHelper

class ProfileFragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var profileImageView: ImageView
    private lateinit var updateButton: Button
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
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

        // View'ları bağlama
        nameEditText = view.findViewById(R.id.nameEditText)
        usernameEditText = view.findViewById(R.id.usernameEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        profileImageView = view.findViewById(R.id.profileImageView)
        updateButton = view.findViewById(R.id.updateButton)

        // DatabaseHelper başlatma
        dbHelper = DatabaseHelper(requireContext())

        setupImagePicker()
        loadUserProfile()

        // Profil fotoğrafını seçme
        profileImageView.setOnClickListener { checkAndOpenImagePicker() }

        // Güncelleme butonuna tıklama
        updateButton.setOnClickListener {
            handleUpdateProfile()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupImagePicker() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == android.app.Activity.RESULT_OK) {
                    val imageUri = result.data?.data
                    if (imageUri != null) {
                        profileImageView.setImageURI(imageUri)
                        selectedProfileImageUri = imageUri // Fotoğraf URI'sini saklıyoruz
                    }
                }
            }
    }

    private fun checkAndOpenImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // İzin yok, kullanıcıdan isteme
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE_PERMISSION)
            } else {
                // İzin var, dosya seçici aç
                openImagePicker()
            }
        } else {
            // Eski Android sürümleri için direkt dosya seçici aç
            openImagePicker()
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

            // Profil fotoğrafı varsa göster
            user.profilePhoto?.let {
                val imageUri = Uri.parse(it)
                profileImageView.setImageURI(imageUri)
            }
        } else {
            Toast.makeText(requireContext(), "Kullanıcı bilgileri yüklenemedi", Toast.LENGTH_SHORT).show()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Kullanıcı izni verdi, dosya seçici aç
                openImagePicker()
            } else {
                Toast.makeText(requireContext(), "İzin reddedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1001
    }
}
