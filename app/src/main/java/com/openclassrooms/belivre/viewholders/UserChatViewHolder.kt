package com.openclassrooms.belivre.viewholders

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.chat.UserChatChannel
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import com.readystatesoftware.viewbadger.BadgeView
import kotlinx.android.synthetic.main.chat_fragment_item_row.view.*

class UserChatViewHolder (v: View) : RecyclerView.ViewHolder(v) {
    private var view: View = v
    private var userChatChannel : UserChatChannel? = null

    fun bindUser(userChatChannel: UserChatChannel){
        this.userChatChannel = userChatChannel
        view.userDNChat.text = userChatChannel.displayName

        if(!userChatChannel.lastMessage.isNullOrEmpty()){
            view.messageChatValue.text = userChatChannel.lastMessage
        }
        else{
            view.messageChatValue.text = ""
        }

        if (!userChatChannel.seen) {
            val badgeView = BadgeView(view.context, view.userPicChat)
            badgeView.badgeBackgroundColor = ContextCompat.getColor(view.context, R.color.colorAccent)
            badgeView.text = "1"
            badgeView.setTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
            badgeView.badgePosition = BadgeView.POSITION_TOP_RIGHT
            badgeView.textSize = 12f
            badgeView.show()

            view.messageChatValue.setTextColor(Color.WHITE)
            view.userDNChat.setTypeface(null, Typeface.BOLD)
            view.ccLL.background = ContextCompat.getDrawable(view.context, R.drawable.rect_round_primary_transparent_color)
            view.messageChatValue.setTypeface(null, Typeface.BOLD)
            view.userDNChat.setTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
        }

        loadProfilePictureIntoImageView(view.userPicChat, view.context, userChatChannel.profilePic, userChatChannel.userId.toString())
    }
}