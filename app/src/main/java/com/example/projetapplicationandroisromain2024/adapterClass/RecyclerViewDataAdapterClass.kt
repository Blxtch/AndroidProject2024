package com.example.projetapplicationandroisromain2024.adapterClass

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.projetapplicationandroisromain2024.DataBaseHelper
import com.example.projetapplicationandroisromain2024.R
import com.example.projetapplicationandroisromain2024.dataClass.DataClass

class RecyclerViewDataAdapterClass(
    private val dataList: ArrayList<DataClass>,
    private val isAdmin: Boolean,
    private val dbHelper: DataBaseHelper
) :
    RecyclerView.Adapter<RecyclerViewDataAdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        holder.rvBrand.text = currentItem.dataBrand
        holder.rvLink.text = currentItem.dataLink
        holder.rvItemId.text = "Identification number :"+ currentItem.dataId.toString()

        val indicator = holder.rvAvailabilityIndicator
        if (currentItem.dataIsAvailable) {
            indicator.setBackgroundResource(R.drawable.circle_shape_green)
        } else {
            indicator.setBackgroundResource(R.drawable.circle_shape_red)
        }

        holder.rvLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(currentItem.dataLink)
            holder.rvLink.context.startActivity(intent)
        }

        // Handle item type and set appropriate icon
        if (currentItem.dataType == "Phone") {
            holder.rvImage.setImageResource(R.drawable.icon_phone)
        } else {
            holder.rvImage.setImageResource(R.drawable.tablet_icon)
        }

        holder.loanButton.setOnClickListener {
            val isNowAvailable = !currentItem.dataIsAvailable
            val success = dbHelper.updateAvailabilityItem(currentItem.dataId, isNowAvailable)

            if (success) {
                // Update the item's availability locally
                currentItem.dataIsAvailable = isNowAvailable
                notifyItemChanged(position) // Refresh the view for this item
            }
        }


        holder.itemView.setOnClickListener {
            val hiddenSection = holder.hiddenSection
            if (hiddenSection.visibility == View.VISIBLE) {

                hiddenSection.visibility = View.GONE
            } else {

                hiddenSection.visibility = View.VISIBLE
            }
        }

        val adapter = ArrayAdapter.createFromResource(
            holder.itemView.context,
            R.array.items_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerType.adapter = adapter


        if (isAdmin) {
            holder.editButton.visibility = View.VISIBLE
            holder.editButton.setOnClickListener {
                holder.editSection.visibility = View.VISIBLE
                holder.rvBrand.visibility = View.GONE
                holder.rvLink.visibility = View.GONE
                holder.rvImage.visibility = View.GONE
                holder.editButton.visibility = View.GONE


                holder.editTitle.setText(currentItem.dataBrand)
                holder.editLink.setText(currentItem.dataLink)

            }
        } else {
            holder.editButton.visibility = View.GONE
        }

        holder.saveButton.setOnClickListener {
            val newTitle = holder.editTitle.text.toString()
            val newLink = holder.editLink.text.toString()
            val newType = holder.spinnerType.selectedItem.toString()

            val dbHelper = DataBaseHelper(holder.itemView.context)
            dbHelper.updateItem(currentItem.dataId, newTitle, newLink, newType)

            currentItem.dataBrand = newTitle
            currentItem.dataLink = newLink
            currentItem.dataType = newType
            notifyItemChanged(position)

            holder.editSection.visibility = View.GONE
            holder.rvBrand.visibility = View.VISIBLE
            holder.rvLink.visibility = View.VISIBLE
            holder.rvImage.visibility = View.VISIBLE
            holder.editButton.visibility = View.VISIBLE

        }

        holder.deleteButton.setOnClickListener {
            val dbHelper = DataBaseHelper(holder.itemView.context)
            dbHelper.deleteItem(currentItem.dataId)
            dataList.removeAt(position)
            // Notify the adapter about the removal
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, dataList.size) // Adjust the indices for the remaining items
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvBrand: TextView = itemView.findViewById(R.id.title)
        val rvLink: TextView = itemView.findViewById(R.id.link)
        val rvImage: ImageView = itemView.findViewById(R.id.image)
        val rvAvailabilityIndicator: View = itemView.findViewById(R.id.availability)

        // Hidden section (unique ID and loan button)
        val hiddenSection: LinearLayout = itemView.findViewById(R.id.hiddenSection)
        val rvItemId: TextView = itemView.findViewById(R.id.itemId)
        val loanButton: Button = itemView.findViewById(R.id.loanButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val editTitle: EditText = itemView.findViewById(R.id.editTitle)
        val editLink: EditText = itemView.findViewById(R.id.editLink)
        val editSection: LinearLayout =itemView.findViewById(R.id.editSection)
        val saveButton: Button = itemView.findViewById(R.id.saveButton)
        val spinnerType: Spinner = itemView.findViewById(R.id.type_spinner)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }
}