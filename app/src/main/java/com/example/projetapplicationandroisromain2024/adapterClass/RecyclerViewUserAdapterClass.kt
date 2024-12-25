package com.example.projetapplicationandroisromain2024.adapterClass

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projetapplicationandroisromain2024.R

class RecyclerViewUserAdapterClass {

    // User data class (Assuming you already have a User data class)
    data class User(val username: String, val email: String, val role: String)

    // Adapter for the RecyclerView
    class UserAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

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



        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
            return UserViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = userList[position]
            holder.usernameTextView.text = user.username
            holder.emailTextView.text = user.email
            holder.roleTextView.text = user.role

            if (user.role.toInt() == 0) {
                holder.deleteButtonUsers.visibility = View.GONE
                holder.spinnerRole.visibility = View.GONE

            }

            holder.editButtonUsers.setOnClickListener {
                holder.editSectionUsers.visibility = View.VISIBLE

            }

            holder.deleteButtonUsers.setOnClickListener {
                holder.editSectionUsers.visibility = View.GONE
            }
        }

        // Return the size of the dataset (invoked by the layout manager)
        override fun getItemCount(): Int {
            return userList.size
        }
    }
}