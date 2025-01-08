package com.example.projetapplicationandroisromain2024.Authentification

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetapplicationandroisromain2024.DataBaseHelper

import com.example.projetapplicationandroisromain2024.databinding.ActivitySignupUserBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupUserBinding
    private lateinit var dataBaseHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataBaseHelper = DataBaseHelper(this)

        binding.signUpButton.setOnClickListener {
            val signupEmail = binding.UserEmailInput.text.toString()
            val signupPassword = binding.UserPasswordInput.text.toString()
            val name = binding.UsernameInput.text.toString()

            if (signupPassword.length < 4) {
                Toast.makeText(
                    this,
                    "Password is too short. Must have a minimum of 4 characters.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (signupEmail.isBlank() || signupPassword.isBlank()) {
                Toast.makeText(this, "Email and Password cannot be empty.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val role = 1
            val result = dataBaseHelper.insertUser(name, signupPassword, role, signupEmail)

            if (result > 0) {
                Toast.makeText(this, "User created successfully.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Error creating User.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}