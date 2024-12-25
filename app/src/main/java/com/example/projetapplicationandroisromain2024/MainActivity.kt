package com.example.projetapplicationandroisromain2024

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.projetapplicationandroisromain2024.adapterClass.RecyclerViewDataAdapterClass
import com.example.projetapplicationandroisromain2024.dataClass.DataClass
import com.example.projetapplicationandroisromain2024.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataBaseHelper: DataBaseHelper
    private var loggedInUser: String? = null
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var recyclerView: RecyclerViewDataAdapterClass
    private var isAdmin: Boolean = false // Initialize here but set correctly later

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataBaseHelper = DataBaseHelper(this)
        loggedInUser = intent.getStringExtra("username")

        // Properly determine the isAdmin value
        isAdmin = loggedInUser?.let { dataBaseHelper.getUserRole(it)!= 1 } ?: false
        val isSuperUser = loggedInUser?.let {dataBaseHelper.getUserRole(it) == 0?: false}
        // Show or hide admin-specific buttons
        if (isAdmin) {
            binding.addMaterialBtn.visibility = View.VISIBLE


            binding.addUserBtn.setOnClickListener {
                val intent = Intent(this, admin_activity::class.java)
                startActivity(intent)
            }
            binding.addMaterialBtn.setOnClickListener {
                val intent = Intent(this, addMaterialActivity::class.java)
                startActivity(intent)
            }
        } else {
            binding.addMaterialBtn.visibility = View.GONE

        }

        if(isSuperUser == true) {
            binding.addUserBtn.visibility = View.VISIBLE
        }else{
            binding.addUserBtn.visibility = View.GONE
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
            val type = item.dataType // Material name
            val link = item.dataLink // Merchant link (used as an image placeholder here)
            val brand = item.dataBrand
            val isAvailable = item.dataIsAvailable
            val uniqueId = item.dataId

            dataList.add(DataClass(type, link, brand, isAvailable, uniqueId))
        }

        recyclerView = RecyclerViewDataAdapterClass(dataList, isAdmin, dataBaseHelper)
        binding.recyclerViewInventory.apply {
            adapter = recyclerView
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity)
        }
    }
}