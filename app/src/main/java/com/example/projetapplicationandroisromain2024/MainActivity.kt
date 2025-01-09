package com.example.projetapplicationandroisromain2024

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.projetapplicationandroisromain2024.Authentification.LoginActivity
import com.example.projetapplicationandroisromain2024.InventoryAndUsersManagement.AddMaterialActivity
import com.example.projetapplicationandroisromain2024.RecyclerViewAdapter.RecyclerViewItemsAdapterClass
import com.example.projetapplicationandroisromain2024.DataClasses.DataClassItems
import com.example.projetapplicationandroisromain2024.InventoryAndUsersManagement.UserAdministrationActivity
import com.example.projetapplicationandroisromain2024.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataBaseHelper: DataBaseHelper
    private var loggedInUser: String? = null
    private lateinit var dataList: ArrayList<DataClassItems>
    private lateinit var recyclerView: RecyclerViewItemsAdapterClass
    private var isAdmin: Boolean = false
    private var isSuperUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataBaseHelper = DataBaseHelper(this)
        loggedInUser = intent.getStringExtra("username")

        binding.logoutButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }

        binding.zxingBarcodeScanner.setOnClickListener {
            val intent = Intent(this, QrCodeScannerActivity::class.java)
            startActivity(intent)
        }

        isAdmin = loggedInUser?.let { dataBaseHelper.getUserRole(it) != 1 } ?: false
        isSuperUser = loggedInUser?.let { dataBaseHelper.getUserRole(it) == 0 } ?: false

        if (isAdmin) {
            binding.addMaterialBtn.visibility = View.VISIBLE
            binding.addMaterialBtn.setOnClickListener {
                val intent = Intent(this, AddMaterialActivity::class.java)
                startActivity(intent)
            }

            if(isSuperUser) {
                binding.addUserBtn.visibility = View.VISIBLE
                binding.addUserBtn.setOnClickListener {
                    val intent = Intent(this, UserAdministrationActivity::class.java)
                    startActivity(intent)}
            }else{
                binding.addUserBtn.visibility = View.GONE
            }
        } else {
            binding.addMaterialBtn.visibility = View.GONE

        }
        displayItems()
    }

    override fun onResume() {
        super.onResume()
        displayItems()
    }

    private fun displayItems() {
        val items = dataBaseHelper.getAllItems()

        dataList = ArrayList()
        for (item in items) {
            val ref = item.dataRef
            val type = item.dataType
            val link = item.dataLink
            val brand = item.dataBrand
            val isAvailable = item.dataIsAvailable
            val uniqueId = item.dataId

            dataList.add(DataClassItems(ref,type, link, brand, isAvailable, uniqueId))
        }

        recyclerView = RecyclerViewItemsAdapterClass(dataList, isAdmin, dataBaseHelper)
        binding.recyclerViewInventory.apply {
            adapter = recyclerView
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity)
        }
    }
}