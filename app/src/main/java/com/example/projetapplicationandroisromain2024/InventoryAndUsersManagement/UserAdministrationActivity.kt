package com.example.projetapplicationandroisromain2024.InventoryAndUsersManagement

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetapplicationandroisromain2024.databinding.ActivityUserAdministrationBinding
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetapplicationandroisromain2024.DataBaseHelper
import com.example.projetapplicationandroisromain2024.RecyclerViewAdapter.RecyclerViewUserAdapterClass
import com.example.projetapplicationandroisromain2024.DataClasses.DataClassUsers
import com.example.projetapplicationandroisromain2024.R

class UserAdministrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserAdministrationBinding
    private lateinit var dataBaseHelper: DataBaseHelper
    private lateinit var dataList: ArrayList<DataClassUsers>
    private lateinit var recyclerView: RecyclerViewUserAdapterClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserAdministrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataList = ArrayList()
        recyclerView = RecyclerViewUserAdapterClass(dataList)
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.usersRecyclerView.adapter = recyclerView
        dataBaseHelper = DataBaseHelper(this)

        val roleChoices = resources.getStringArray(R.array.role_choices)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roleChoices)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.roleSpinner.adapter = adapter

        val userAdapter = RecyclerViewUserAdapterClass(dataList)
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.usersRecyclerView.adapter = userAdapter

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
            if (password.count() < 4) {
                Toast.makeText(this, "Password to small minimum 4 characters",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (validateInputs(username, password, mail)) {
                addUser(username, password, role.toInt(), mail)
            }
        }
    }

    private fun validateInputs(username: String, password: String, mail: String): Boolean {
        return if (username.isEmpty() || password.isEmpty() || mail.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun addUser(username: String, password: String, role: Int, mail: String) {
        val result = dataBaseHelper.insertUser(username, password, role, mail)
        if (result != -1L) {
            Toast.makeText(this, "User $username added successfully", Toast.LENGTH_SHORT).show()
            onResume()
        } else {
            Toast.makeText(this, "Failed to add user (duplicate username?)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        displayUsers()
    }

    private fun displayUsers() {
        val users = dataBaseHelper.getAllUsers()
        dataList = ArrayList()
        for (user in users) {
            val id = user.id
            val username = user.username
            val role = user.role
            val email = user.email
            val password = user.password

            dataList.add(DataClassUsers(id, username, email, password, role))
        }

        recyclerView = RecyclerViewUserAdapterClass(dataList)
        binding.usersRecyclerView.apply {
            adapter = recyclerView
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@UserAdministrationActivity)
        }
    }
}