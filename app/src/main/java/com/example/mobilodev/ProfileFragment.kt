package com.example.mobilodev

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
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

class UpdateProfileFragment : Fragment() {
    private lateinit var editTextTextPersonName2: EditText
    private lateinit var editTextTextPersonName: EditText
    private lateinit var editTextTextPassword: EditText
    private lateinit var profil: ImageView
    private lateinit var GuncelButon: Button
    private lateinit var db: SQLiteDatabase
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedProfileImage: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextTextPersonName2 = view.findViewById(R.id.editTextTextPersonName2)
        editTextTextPersonName = view.findViewById(R.id.editTextTextPersonName)
        editTextTextPassword = view.findViewById(R.id.editTextTextPassword)
        profil = view.findViewById(R.id.profil)
        GuncelButon = view.findViewById(R.id.GuncelButon)

        // Initialize database
        db = requireContext().openOrCreateDatabase("mobilyazilimfinal.db", Context.MODE_PRIVATE, null)

        // Image picker setup
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val imageUri = result.data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                profil.setImageBitmap(bitmap)
                selectedProfileImage = bitmap
            }
        }

        profil.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        GuncelButon.setOnClickListener {
            val name = editTextTextPersonName2.text.toString()
            val username = editTextTextPersonName.text.toString()
            val password = editTextTextPassword.text.toString()

            if (name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                updateUser(name, username, password)
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUser(name: String, username: String, password: String) {
        try {
            val updateQuery = "UPDATE users SET name = ?, password = ? WHERE username = ?"
            val statement = db.compileStatement(updateQuery)
            statement.bindString(1, name)
            statement.bindString(2, password)
            statement.bindString(3, username)
            val rowsAffected = statement.executeUpdateDelete()

            if (rowsAffected > 0) {
                Toast.makeText(requireContext(), "Profil güncellendi", Toast.LENGTH_SHORT).show()
                // Handle image update if needed
                // Save the selected profile image to the database or local storage
            } else {
                Toast.makeText(requireContext(), "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show()
        }
    }
}
