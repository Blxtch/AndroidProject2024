package com.example.projetapplicationandroisromain2024

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetapplicationandroisromain2024.databinding.ActivityLoginBinding


class login_activity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dataBaseHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataBaseHelper = DataBaseHelper(this)

        // Check if super user exists, if not create one
        if (!dataBaseHelper.isSuperUserCreated()) {
            binding.signupPage.visibility = View.VISIBLE
            binding.loginPage.visibility = View.GONE
            createSuperUser()
        }else {
            binding.signupPage.visibility = View.GONE
            binding.loginPage.visibility = View.VISIBLE
        }

        binding.loginBtn.setOnClickListener {
            val loginUsername = binding.usernameInput.text.toString()
            val loginPassword = binding.passwordInput.text.toString()
            login(loginUsername, loginPassword)
        }
    }

    private fun createSuperUser() {
        binding.signUpButton.setOnClickListener {
            val signupEmail = binding.SUEmailInput.text.toString()
            val signupPassword = binding.SUPasswordInput.text.toString()

            if (signupPassword.count() < 4) {
                Toast.makeText(this,"Password is too short must have a minimum of 4 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (signupEmail.isBlank() || signupPassword.isBlank()) {
                Toast.makeText(this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val role = 0 // Superuser role
            val result = dataBaseHelper.insertUser(signupEmail, signupPassword, role, signupEmail)
            if (result > 0) {
                Toast.makeText(this, "Superuser created successfully", Toast.LENGTH_LONG).show()
                binding.signupPage.visibility = View.GONE
                binding.loginPage.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Error creating superuser", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun login(username: String, password: String) {
        if (dataBaseHelper.isUserValid(username, password)) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("username", username) // Pass the username to MainActivity
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
    }
}