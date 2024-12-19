package com.example.mobilodev

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.io.ByteArrayOutputStream
import java.io.IOException

class SignUpFragment : Fragment() {

    private var editTextTextPersonName2: EditText? = null
    private var editTextTextPersonName: EditText? = null
    private var editTextTextPassword: EditText? = null
    private var editTextTextPassword2: EditText? = null
    private var imageView4: ImageView? = null
    private var profil: ImageView? = null
    private var selectedImageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextTextPersonName = view.findViewById(R.id.editTextTextPersonName)
        editTextTextPersonName2 = view.findViewById(R.id.editTextTextPersonName2)
        editTextTextPassword = view.findViewById(R.id.editTextTextPassword)
        editTextTextPassword2 = view.findViewById(R.id.editTextTextPassword2)
        imageView4 = view.findViewById(R.id.imageView4)
        profil = view.findViewById(R.id.profil)

        // Resim seçme işlemi başlatıcı
        val imagePickerLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val imageUri: Uri? = result.data!!.data
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                    profil?.setImageBitmap(selectedImageBitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Resim seçilemedi", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Profil resmine tıklanınca galeri açılır
        profil?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        imageView4?.setOnClickListener {
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

            // Eğer profil resmi seçilmemişse, standart resmi kullan
            if (selectedImageBitmap == null) {
                selectedImageBitmap = BitmapFactory.decodeResource(resources, R.drawable.standart)
            }

            // Bitmap'i Base64 stringine dönüştür
            val imageBase64 = bitmapToBase64(selectedImageBitmap!!)

            val db: SQLiteDatabase = requireContext().openOrCreateDatabase("mobilyazilimfinal.db", Context.MODE_PRIVATE, null)
            db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, name TEXT, password TEXT, profile_picture TEXT)")

            val values = ContentValues()
            values.put("username", username)
            values.put("name", name)
            values.put("password", password)
            values.put("profile_picture", imageBase64)

            val result = db.insert("users", null, values)
            db.close()

            if (result != -1L) {
                Toast.makeText(requireContext(), "Kayıt başarılı", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Kayıt başarısız", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Bitmap'i Base64 stringine dönüştüren metod
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
