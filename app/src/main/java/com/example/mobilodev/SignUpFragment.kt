package com.example.mobilodev

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.DatabaseHelper
import java.io.IOException

class SignUpFragment : Fragment() {

    private var editTextTextPersonName2: EditText? = null
    private var editTextTextPersonName: EditText? = null
    private lateinit var textLogin: TextView
    private var editTextTextPassword: EditText? = null
    private var editTextTextPassword2: EditText? = null
    private var imageView1: ImageView? = null
    private var profil: ImageView? = null
    private var selectedImageBitmap: Bitmap? = null
    private var selectedImagePath: String? = null
    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextTextPersonName = view.findViewById(R.id.editTextTextPersonName2)
        editTextTextPersonName2 = view.findViewById(R.id.editTextTextPersonName2)
        textLogin = view.findViewById(R.id.textLogin)
        editTextTextPassword = view.findViewById(R.id.editTextTextPassword)
        editTextTextPassword2 = view.findViewById(R.id.editTextTextPassword2)
        imageView1 = view.findViewById(R.id.imageView1)
        profil = view.findViewById(R.id.profil)

        databaseHelper = DatabaseHelper(requireContext())

        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val imageUri: Uri? = result.data!!.data
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(requireActivity().contentResolver, imageUri!!)
                        selectedImageBitmap = ImageDecoder.decodeBitmap(source)
                    } else {
                        @Suppress("DEPRECATION")
                        selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                    }
                    profil?.setImageBitmap(selectedImageBitmap)
                    selectedImagePath = getPathFromUri(imageUri!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Resim seçilemedi", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profil?.setOnClickListener {
            checkPermissionsAndPickImage()
        }

        imageView1?.setOnClickListener {
            val username = editTextTextPersonName2?.text.toString()
            val name = editTextTextPersonName?.text.toString()
            val password = editTextTextPassword?.text.toString()
            val password2 = editTextTextPassword2?.text.toString()

            if (username.isEmpty() || name.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                Toast.makeText(requireContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != password2) {
                Toast.makeText(requireContext(), "Şifreler uyuşmuyor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImagePath == null) {
                Toast.makeText(requireContext(), "Lütfen bir profil resmi seçin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isInserted = databaseHelper.registerUser(username, name, password, selectedImagePath!!)

            if (isInserted) {
                Toast.makeText(requireContext(), "Kayıt başarılı", Toast.LENGTH_SHORT).show()
                databaseHelper.setCurrentUser(username)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MainFragment())
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Kayıt başarısız", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionsAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES), 1)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(requireContext(), "İzin verilmedi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = requireActivity().contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        return path
    }
}
