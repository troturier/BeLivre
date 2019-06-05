package com.openclassrooms.belivre.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import kotlinx.android.synthetic.main.chat_fragment_item_row.view.*

class UserChatViewHolder (v: View) : RecyclerView.ViewHolder(v) {
    private var view: View = v
    private var user : User? = null

    fun bindUser(user: User){
        this.user = user
        view.userDNChat.text = view.context.getString(R.string.profile_display_name, user.firstname, user.lastname?.substring(0,1))

        loadProfilePictureIntoImageView(view.userPicChat, view.context, user.profilePicURL, user.id.toString())
    }
}