package com.example.projetapplicationandroisromain2024.adapterClass

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.projetapplicationandroisromain2024.DataBaseHelper
import com.example.projetapplicationandroisromain2024.R
import com.example.projetapplicationandroisromain2024.dataClass.DataClassUsers

class RecyclerViewUserAdapterClass(
    private val userList: ArrayList<DataClassUsers>

):
    RecyclerView.Adapter<RecyclerViewUserAdapterClass.UserViewHolder>()  {
    // Adapter for the RecyclerView


        // ViewHolder to bind the views for each user
        class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val usernameTextView: TextView = view.findViewById(R.id.name)
            val emailTextView: TextView = view.findViewById(R.id.email)
            val roleTextView: TextView = view.findViewById(R.id.role)

            val saveButtonUsers: Button = view.findViewById(R.id.saveButtonUsers)
            val deleteButtonUsers: Button = view.findViewById(R.id.deleteButtonUsers)
            val editSectionUsers : LinearLayout = view.findViewById(R.id.editSectionUsers)
            val editButtonUsers : Button = view.findViewById((R.id.editButtonUsers))
            val spinnerRole: Spinner = view.findViewById((R.id.role_spinner))

            val editName: EditText = view.findViewById(R.id.editName)
            val editMail: EditText = view.findViewById(R.id.editEmail)
            val editPassword: EditText = view.findViewById(R.id.editPassword)
            val image: ImageView = view.findViewById(R.id.image)
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
            return UserViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = userList[position]
            val dbHelper = DataBaseHelper(holder.itemView.context)
            // Display current user details
            holder.usernameTextView.text = user.username
            holder.emailTextView.text = user.email
            when (user.role) {
                0 -> holder.roleTextView.text = "Super User"
                1 -> holder.roleTextView.text = "User"
                else -> holder.roleTextView.text = "Admin"
            }

            // Hide controls for Super User
            if (user.role == 0) {
                holder.deleteButtonUsers.visibility = View.GONE
                holder.spinnerRole.visibility = View.GONE
            }

            // Setup Spinner Adapter
            val adapter = ArrayAdapter.createFromResource(
                holder.itemView.context,
                R.array.role_choices,
                android.R.layout.simple_spinner_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.spinnerRole.adapter = adapter

            // Set current role as spinner selection
            holder.spinnerRole.setSelection(user.role - 1)

            // Show edit section when edit button is clicked
            holder.editButtonUsers.setOnClickListener {
                holder.editSectionUsers.visibility = View.VISIBLE
                holder.roleTextView.visibility = View.GONE
                holder.usernameTextView.visibility= View.GONE
                holder.emailTextView.visibility = View.GONE
                holder.image.visibility = View.GONE
                holder.editButtonUsers.visibility = View.GONE
                holder.editName.setText(user.username)
                holder.editMail.setText(user.email)
                holder.editPassword.setText((user.password))
            }

            holder.saveButtonUsers.setOnClickListener {
                val newUsername = holder.editName.text.toString()
                val newMail = holder.editMail.text.toString()
                val newPassword = holder.editPassword.text.toString().ifEmpty { user.password }
                val newRole = holder.spinnerRole.selectedItemPosition

                // Validate inputs
                if (newUsername.isEmpty()) {
                    holder.editName.error = "Username cannot be empty"
                    return@setOnClickListener
                }
                if (newMail.isEmpty()) {
                    holder.editMail.error = "Email cannot be empty"
                    return@setOnClickListener
                }
                if (newPassword.isEmpty()) {
                    holder.editPassword.error = "Password cannot be empty"
                    return@setOnClickListener
                }
                if (newPassword.length < 4) {
                    holder.editPassword.error = "Password must have at least 4 characters"
                    return@setOnClickListener
                }

                if (dbHelper.isEmailTaken(newMail, user.id)) {
                    holder.editMail.error = "This email is already in use"
                    return@setOnClickListener
                }

                // Update in database
                val isUpdated = dbHelper.updateUser(user.id, newUsername, newMail, newPassword, newRole + 1)

                if (isUpdated) {
                    // Update local data
                    user.username = newUsername
                    user.email = newMail
                    user.role = newRole + 1
                    user.password = newPassword

                    notifyItemChanged(position) // Ensure the view is refreshed
                } else {
                    Toast.makeText(holder.itemView.context, "Update failed", Toast.LENGTH_SHORT).show()
                }

                // Hide edit section
                holder.editSectionUsers.visibility = View.GONE
                holder.roleTextView.visibility = View.VISIBLE
                holder.usernameTextView.visibility = View.VISIBLE
                holder.emailTextView.visibility = View.VISIBLE
                holder.image.visibility = View.VISIBLE
                holder.editButtonUsers.visibility = View.VISIBLE
            }
            // Delete user
            holder.deleteButtonUsers.setOnClickListener {

                dbHelper.deleteUser(user.username)
                //delete by id please romain
                userList.removeAt(position)

                // Notify adapter about item removal
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, userList.size)
            }
        }

        // Return the size of the dataset (invoked by the layout manager)
        override fun getItemCount(): Int {
            return userList.size
        }

}