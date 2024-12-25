package com.example.projetapplicationandroisromain2024

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetapplicationandroisromain2024.databinding.ActivityAddMaterialBinding

class addMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMaterialBinding
    private lateinit var dataBaseHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataBaseHelper = DataBaseHelper(this)

        // Setup the Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.items_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typeSpinner.adapter = adapter

        // Handle save button click
        binding.saveMaterialBtn.setOnClickListener {
            val brand = binding.materialNameInput.text.toString()
            val link = binding.materialLinkInput.text.toString()
            val name = binding.typeSpinner.selectedItem.toString() // Get selected type

            if (name.isEmpty()) {
                Toast.makeText(this, "Material name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dataBaseHelper.insertItem(name, link, true, brand)
            if (result != -1L) {
                Toast.makeText(this, "Material added successfully", Toast.LENGTH_SHORT).show()
                finish() // Close this activity after adding material
            } else {
                Toast.makeText(this, "Failed to add material", Toast.LENGTH_SHORT).show()
            }
        }
    }
    }
