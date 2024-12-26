package com.example.mobilodev

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.myapplication.DatabaseHelper

class LoginFragment : Fragment() {
    private lateinit var editTextTextPersonName: EditText
    private lateinit var editTextTextPassword: EditText
    private lateinit var imageView4: ImageView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var textView5: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextTextPersonName = view.findViewById(R.id.editTextTextPersonName2)
        editTextTextPassword = view.findViewById(R.id.editTextTextPassword)
        imageView4 = view.findViewById(R.id.imageView4)
        textView5 = view.findViewById(R.id.textView5)

        textView5.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignUpFragment())
                .addToBackStack(null)
                .commit()
        }

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(requireContext())

        imageView4.setOnClickListener {
            val username = editTextTextPersonName.text.toString()
            val password = editTextTextPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val user = databaseHelper.loginUser(username, password)
                if (user != null) {
                    // Login successful
                    Toast.makeText(requireContext(), "Giriş başarılı", Toast.LENGTH_SHORT).show()
                    databaseHelper.setCurrentUser(username)
                    // Navigate to the next screen or perform actions on successful login
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MainFragment()) // Adjust the container ID if necessary
                        .commit()
                } else {
                    // Invalid credentials
                    Toast.makeText(requireContext(), "Hatalı kullanıcı adı veya şifre", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
