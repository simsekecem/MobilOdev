package com.example.mobilodev

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Context
import android.content.Intent
import java.io.FileOutputStream
import android.widget.ImageView

class LoginFragment : Fragment() {
    private lateinit var editTextTextPersonName: EditText
    private lateinit var editTextTextPassword: EditText
    private lateinit var imageView4: ImageView
    private lateinit var db: SQLiteDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextTextPersonName = view.findViewById(R.id.editTextTextPersonName)
        editTextTextPassword = view.findViewById(R.id.editTextTextPassword)
        imageView4 = view.findViewById(R.id.imageView4)

        // Copy database if it doesn't exist
        copyDatabase(requireContext())

        // Open the database
        db = requireContext().openOrCreateDatabase("mobilyazilimfinal.db", Context.MODE_PRIVATE, null)

        imageView4.setOnClickListener {
            val username = editTextTextPersonName.text.toString()
            val password = editTextTextPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val query = "SELECT * FROM users WHERE username = ? AND password = ?"
                val cursor: Cursor = db.rawQuery(query, arrayOf(username, password))
                if (cursor.moveToFirst()) {
                    // Giriş başarılı
                    Toast.makeText(requireContext(), "Giriş başarılı", Toast.LENGTH_SHORT).show()
                } else {
                    // Hatalı giriş
                    Toast.makeText(requireContext(), "Hatalı kullanıcı adı veya şifre", Toast.LENGTH_SHORT).show()
                }
                cursor.close()
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to copy the database from assets to the app's data folder
    private fun copyDatabase(context: Context) {
        val dbPath = context.getDatabasePath("mobilyazilimfinal.db")
        if (!dbPath.exists()) {
            val inputStream = context.assets.open("mobilyazilimfinal.db")
            val outputStream = FileOutputStream(dbPath)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        }
    }

}
