package com.example.projetapplicationandroisromain2024.RecyclerViewAdapter

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.projetapplicationandroisromain2024.DataBaseHelper
import com.example.projetapplicationandroisromain2024.R
import com.example.projetapplicationandroisromain2024.DataClasses.DataClassItems
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class RecyclerViewItemsAdapterClass(
    private val dataList: ArrayList<DataClassItems>,
    private val isAdmin: Boolean,
    private val dbHelper: DataBaseHelper
) :
    RecyclerView.Adapter<RecyclerViewItemsAdapterClass.ViewHolderClass>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        holder.rvBrand.text = currentItem.dataBrand
        holder.rvLink.text = currentItem.dataLink
        holder.rvItemId.text = "Identification number : " + currentItem.dataRef
        val indicator = holder.rvAvailabilityIndicator
        if (currentItem.dataIsAvailable) {
            indicator.setBackgroundResource(R.drawable.circle_shape_green)
        } else {
            indicator.setBackgroundResource(R.drawable.circle_shape_red)
        }

        val qrWriter = QRCodeWriter()
        try {
            val bitMatrix = qrWriter.encode(currentItem.dataRef, BarcodeFormat.QR_CODE, 200, 200)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            holder.qrCodeImageView.setImageBitmap(bmp)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.rvLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            try {
                intent.data = Uri.parse(currentItem.dataLink)
                holder.rvLink.context.startActivity(intent)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }

        if (currentItem.dataType == "Phone") {
            holder.rvImage.setImageResource(R.drawable.phone_icon)
        } else {
            holder.rvImage.setImageResource(R.drawable.tablet_icon)
        }

        holder.itemView.setOnClickListener {
            val hiddenSection = holder.hiddenSection
            if (hiddenSection.visibility == View.VISIBLE) {
                hiddenSection.visibility = View.GONE
            } else {
                hiddenSection.visibility = View.VISIBLE
            }
        }

        if (isAdmin) {
            holder.editButton.visibility = View.VISIBLE
            holder.editButton.setOnClickListener {
                holder.editSection.visibility = View.VISIBLE
                holder.rvBrand.visibility = View.GONE
                holder.rvLink.visibility = View.GONE
                holder.rvImage.visibility = View.GONE
                holder.editButton.visibility = View.GONE

                holder.editRef.setText(currentItem.dataRef)
                holder.editTitle.setText(currentItem.dataBrand)
                holder.editLink.setText(currentItem.dataLink)

                val adapter = ArrayAdapter.createFromResource(
                    holder.itemView.context,
                    R.array.items_types,
                    android.R.layout.simple_spinner_item
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                holder.spinnerType.adapter = adapter
            }
        } else {
            holder.editButton.visibility = View.GONE
        }

        holder.saveButton.setOnClickListener {
            val newTitle = holder.editTitle.text.toString().trim()
            val newLink = holder.editLink.text.toString().trim()
            val newType = holder.spinnerType.selectedItem.toString()
            val newRef = holder.editRef.text.toString().trim()

            if (newRef.isEmpty()) {
                holder.editRef.error = "Reference cannot be empty"
                return@setOnClickListener
            }
            if (newTitle.isEmpty()) {
                holder.editTitle.error = "Title cannot be empty"
                return@setOnClickListener
            }
            if (newLink.isEmpty()) {
                holder.editLink.error = "Link cannot be empty"
                return@setOnClickListener
            }

            if (dbHelper.isRefAlreadyAttributed(newRef, currentItem.dataId)) {
                holder.editRef.error = "This reference is already attributed to another item."
                return@setOnClickListener
            }

            dbHelper.updateItem(currentItem.dataId, newRef, newTitle, newLink, newType)

            currentItem.dataRef = newRef
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
            notifyItemRemoved(position)
            notifyItemRangeChanged(
                position,
                dataList.size
            )
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

        val hiddenSection: LinearLayout = itemView.findViewById(R.id.hiddenSection)
        val rvItemId: TextView = itemView.findViewById(R.id.itemId)

        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val editTitle: EditText = itemView.findViewById(R.id.editTitle)
        val editLink: EditText = itemView.findViewById(R.id.editLink)
        val editSection: LinearLayout = itemView.findViewById(R.id.editSection)
        val saveButton: Button = itemView.findViewById(R.id.saveButton)
        val spinnerType: Spinner = itemView.findViewById(R.id.type_spinner)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        val qrCodeImageView: ImageView = itemView.findViewById(R.id.qrCodeImageView)
        val editRef: EditText = itemView.findViewById(R.id.editRef)
    }
}