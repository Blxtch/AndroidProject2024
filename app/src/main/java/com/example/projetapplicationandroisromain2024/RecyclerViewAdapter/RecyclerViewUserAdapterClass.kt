package com.example.projetapplicationandroisromain2024.RecyclerViewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.projetapplicationandroisromain2024.DataBaseHelper
import com.example.projetapplicationandroisromain2024.R
import com.example.projetapplicationandroisromain2024.DataClasses.DataClassUsers

class RecyclerViewUserAdapterClass(
    private val userList: ArrayList<DataClassUsers>

):
    RecyclerView.Adapter<RecyclerViewUserAdapterClass.UserViewHolder>()  {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
            return UserViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = userList[position]
            val dbHelper = DataBaseHelper(holder.itemView.context)

            holder.usernameTextView.text = user.username
            holder.emailTextView.text = user.email
            when (user.role) {
                0 -> holder.roleTextView.text = "Super User"
                1 -> holder.roleTextView.text = "User"
                else -> holder.roleTextView.text = "Admin"
            }

            if (user.role == 0) {
                holder.deleteButtonUsers.visibility = View.GONE
                holder.spinnerRole.visibility = View.GONE
            }

            holder.spinnerRole.setSelection(user.role - 1)

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

                val adapter = ArrayAdapter.createFromResource(
                    holder.itemView.context,
                    R.array.role_choices,
                    android.R.layout.simple_spinner_item
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                holder.spinnerRole.adapter = adapter
            }

            holder.saveButtonUsers.setOnClickListener {
                val newUsername = holder.editName.text.toString()
                val newMail = holder.editMail.text.toString()
                val newPassword = holder.editPassword.text.toString().ifEmpty { user.password }
                val newRole = holder.spinnerRole.selectedItemPosition

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

                val isUpdated = dbHelper.updateUser(user.id, newUsername, newMail, newPassword, newRole + 1)

                if (isUpdated) {
                    user.username = newUsername
                    user.email = newMail
                    user.role = newRole + 1
                    user.password = newPassword

                    notifyItemChanged(position)
                } else {
                    Toast.makeText(holder.itemView.context, "Update failed", Toast.LENGTH_SHORT).show()
                }

                holder.editSectionUsers.visibility = View.GONE
                holder.roleTextView.visibility = View.VISIBLE
                holder.usernameTextView.visibility = View.VISIBLE
                holder.emailTextView.visibility = View.VISIBLE
                holder.image.visibility = View.VISIBLE
                holder.editButtonUsers.visibility = View.VISIBLE
            }

            holder.deleteButtonUsers.setOnClickListener {
                dbHelper.deleteUser(user.id)
                userList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, userList.size)
            }
        }

        override fun getItemCount(): Int {
            return userList.size
        }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameTextView: TextView = view.findViewById(R.id.name)
        val emailTextView: TextView = view.findViewById(R.id.email)
        val roleTextView: TextView = view.findViewById(R.id.role)

        val saveButtonUsers: Button = view.findViewById(R.id.saveButtonUsers)
        val deleteButtonUsers: ImageButton = view.findViewById(R.id.deleteButtonUsers)
        val editSectionUsers : LinearLayout = view.findViewById(R.id.editSectionUsers)
        val editButtonUsers : ImageButton = view.findViewById((R.id.editButtonUsers))
        val spinnerRole: Spinner = view.findViewById((R.id.role_spinner))

        val editName: EditText = view.findViewById(R.id.editName)
        val editMail: EditText = view.findViewById(R.id.editEmail)
        val editPassword: EditText = view.findViewById(R.id.editPassword)
        val image: ImageView = view.findViewById(R.id.image)
    }

}