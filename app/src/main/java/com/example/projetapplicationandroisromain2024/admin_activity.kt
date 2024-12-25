package com.example.projetapplicationandroisromain2024

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetapplicationandroisromain2024.databinding.ActivityAdminBinding
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetapplicationandroisromain2024.adapterClass.RecyclerViewUserAdapterClass

class admin_activity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var dataBaseHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize DataBaseHelper
        dataBaseHelper = DataBaseHelper(this)

        // Set up the role spinner
        val roleChoices = resources.getStringArray(R.array.role_choices)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roleChoices)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.roleSpinner.adapter = adapter

        // Set up the RecyclerView
        val userList = getUsers()  // Get users from the database
        val userAdapter = RecyclerViewUserAdapterClass.UserAdapter(userList)
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.usersRecyclerView.adapter = userAdapter

        // Button click listener for adding a new user
        binding.addUserBtn.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val mail = binding.mailInput.text.toString()
            var role = binding.roleSpinner.selectedItem.toString() // Convert role to string
            if (role == "User") {
                role = "1"
            } else {
                role = "2"
            }

            if (validateInputs(username, password, mail)) {
                addUser(username, password, role.toInt(), mail)
            }
        }
    }

    // Validate input fields
    private fun validateInputs(username: String, password: String, mail: String): Boolean {
        return if (username.isEmpty() || password.isEmpty() || mail.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    // Add a user to the database
    private fun addUser(username: String, password: String, role: Int, mail: String) {
        val result = dataBaseHelper.insertUser(username, password, role, mail)
        if (result != -1L) {
            Toast.makeText(this, "User $username added successfully", Toast.LENGTH_SHORT).show()
            updateUserList()  // Update the RecyclerView with the new user
        } else {
            Toast.makeText(this, "Failed to add user (duplicate username?)", Toast.LENGTH_SHORT).show()
        }
    }

    // Update the RecyclerView with the latest list of users
    private fun updateUserList() {
        val userList = getUsers()  // Get updated list of users
        val userAdapter = RecyclerViewUserAdapterClass.UserAdapter(userList)
        binding.usersRecyclerView.adapter = userAdapter
    }

    // Get users from the database
    private fun getUsers(): List<RecyclerViewUserAdapterClass.User> {
        val users = mutableListOf<RecyclerViewUserAdapterClass.User>()
        val db = dataBaseHelper.readableDatabase  // Access readable database

        val cursor = db.rawQuery("SELECT * FROM users", null)

        if (cursor.moveToFirst()) {
            do {
                val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("mail"))
                val role = cursor.getInt(cursor.getColumnIndexOrThrow("role")).toString()  // Convert role to String
                users.add(RecyclerViewUserAdapterClass.User(username, email, role))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()  // Don't forget to close the database when done

        return users
    }
}