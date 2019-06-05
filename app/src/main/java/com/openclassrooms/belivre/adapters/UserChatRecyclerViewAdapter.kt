package com.openclassrooms.belivre.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.chat.UserChatChannel
import com.openclassrooms.belivre.utils.inflate
import com.openclassrooms.belivre.viewholders.UserChatViewHolder

class UserChatRecyclerViewAdapter(private val userChatChannels :List<UserChatChannel>, private val clickListener: (UserChatChannel, Int) -> Unit): RecyclerView.Adapter<UserChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatViewHolder {
        val inflatedView = parent.inflate(R.layout.chat_fragment_item_row, false)
        return UserChatViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return userChatChannels.size
    }

    override fun onBindViewHolder(holder: UserChatViewHolder, position: Int) {
        val itemUserChatChannel = userChatChannels[position]
        holder.bindUser(itemUserChatChannel)
        holder.itemView.setOnClickListener { clickListener(itemUserChatChannel, position) }
    }
}