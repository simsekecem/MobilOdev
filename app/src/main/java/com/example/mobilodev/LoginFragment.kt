package com.example.mobilodev

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.DatabaseHelper

class LoginFragment : Fragment() {

    private lateinit var editTextTextPersonName3: EditText
    private lateinit var editTextTextPassword3: EditText
    private lateinit var imageView2: ImageView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var textView2: TextView // Hatalı türlendirme düzeltilerek TextView yapıldı

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View'leri doğru şekilde bağlama
        editTextTextPersonName3 = view.findViewById(R.id.editTextTextPersonName3)
        editTextTextPassword3 = view.findViewById(R.id.editTextTextPassword3)
        imageView2 = view.findViewById(R.id.imageView2)
        textView2 = view.findViewById(R.id.textView2)

        // TextView'e tıklama özelliği ekleniyor
        textView2.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignUpFragment())
                .addToBackStack(null)
                .commit()
        }

        // DatabaseHelper başlatma
        databaseHelper = DatabaseHelper(requireContext())

        // Giriş butonu (imageView2) tıklama işlemi
        imageView2.setOnClickListener {
            val username = editTextTextPersonName3.text.toString()
            val password = editTextTextPassword3.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val user = databaseHelper.loginUser(username, password)
                if (user != null) {
                    // Giriş başarılı
                    Toast.makeText(requireContext(), "Giriş başarılı", Toast.LENGTH_SHORT).show()
                    databaseHelper.setCurrentUser(username)
                    // Ana ekrana geçiş
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, UserFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    // Geçersiz giriş bilgisi
                    Toast.makeText(requireContext(), "Hatalı kullanıcı adı veya şifre", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Eksik bilgi uyarısı
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
