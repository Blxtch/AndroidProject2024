package com.example.projetapplicationandroisromain2024

import android.content.Intent
import android.os.Bundle
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
            createSuperUser()
        }

        binding.loginBtn.setOnClickListener {
            val loginUsername = binding.usernameInput.text.toString()
            val loginPassword = binding.passwordInput.text.toString()
            login(loginUsername, loginPassword)
        }
    }

    private fun createSuperUser() {
        val username = "admin"
        val password = "admin123"

        dataBaseHelper.insertUser(username, password, 0, username)
        Toast.makeText(this, "Super user created: $username/$password", Toast.LENGTH_LONG).show()
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