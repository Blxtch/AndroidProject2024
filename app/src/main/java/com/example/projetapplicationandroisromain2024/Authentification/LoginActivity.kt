package com.example.projetapplicationandroisromain2024.Authentification

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetapplicationandroisromain2024.DataBaseHelper
import com.example.projetapplicationandroisromain2024.MainActivity
import com.example.projetapplicationandroisromain2024.R
import com.example.projetapplicationandroisromain2024.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dataBaseHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataBaseHelper = DataBaseHelper(this)

        val tvSignUpLink: TextView = findViewById(R.id.tvSignUpLink)
        tvSignUpLink.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

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
                binding.SUPasswordInput.error = "Password is too short must have a minimum of 4 characters"
                return@setOnClickListener
            }

            if (signupEmail.isBlank() || signupPassword.isBlank()) {
                binding.signUpButton.error = "Email and Password cannot be empty"
                return@setOnClickListener
            }
            val role = 0
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
            binding.loginBtn.error = "Invalid username or password"
        }
    }
}