package com.example.projetapplicationandroisromain2024.InventoryAndUsersManagement

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetapplicationandroisromain2024.DataBaseHelper
import com.example.projetapplicationandroisromain2024.R
import com.example.projetapplicationandroisromain2024.databinding.ActivityAddMaterialBinding

class AddMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMaterialBinding
    private lateinit var dataBaseHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataBaseHelper = DataBaseHelper(this)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.items_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typeSpinner.adapter = adapter

        binding.saveMaterialBtn.setOnClickListener {
            val brand = binding.materialNameInput.text.toString()
            val link = binding.materialLinkInput.text.toString()
            val name = binding.typeSpinner.selectedItem.toString()
            val ref = binding.materialRefInput.text.toString()

            if (name.isEmpty()) {
                binding.materialNameInput.error = "Material name is required"
                return@setOnClickListener
            }

            if (dataBaseHelper.isRefAlreadyAttributed(ref)){
                binding.materialRefInput.error = "Ref already attributed"
                return@setOnClickListener
            }

            val result = dataBaseHelper.insertItem(ref, name, link, true, brand)
            if (result != -1L) {
                Toast.makeText(this, "Material added successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add material", Toast.LENGTH_SHORT).show()
            }
        }
    }
    }
