package com.openclassrooms.belivre.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.utils.inflate
import com.openclassrooms.belivre.viewholders.UserChatViewHolder

class UserChatRecyclerViewAdapter(private val users :List<User>, private val clickListener: (User, Int) -> Unit): RecyclerView.Adapter<UserChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatViewHolder {
        val inflatedView = parent.inflate(R.layout.chat_fragment_item_row, false)
        return UserChatViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserChatViewHolder, position: Int) {
        val itemUser = users[position]
        holder.bindUser(itemUser)
        holder.itemView.setOnClickListener { clickListener(itemUser, position) }
    }
}