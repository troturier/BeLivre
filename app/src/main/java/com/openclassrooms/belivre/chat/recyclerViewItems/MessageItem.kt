package com.openclassrooms.belivre.chat.recyclerViewItems

import android.view.Gravity
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.chat.Message
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import org.jetbrains.anko.wrapContent
import java.text.SimpleDateFormat
import java.util.*


abstract class MessageItem(private val message: Message)
    : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        setTimeText(viewHolder)
        setMessageRootGravity(viewHolder)
    }

    private fun setTimeText(viewHolder: ViewHolder) {
        val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentDate = sdfDate.format(Date())
        if(sdfDate.format(message.time) == currentDate){
            viewHolder.textView_message_time.text = sdfTime.format(message.time)
        }
        else{
            viewHolder.textView_message_time.text = sdfDate.format(message.time)
        }
    }

    private fun setMessageRootGravity(viewHolder: ViewHolder) {
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            viewHolder.message_root.apply {
                viewHolder.textView_message_text.background = ContextCompat.getDrawable(viewHolder.containerView.context, R.drawable.rect_round_accent_color)
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.END)
                this.layoutParams = lParams
            }
        }
        else {
            viewHolder.message_root.apply {
                viewHolder.textView_message_text.background = ContextCompat.getDrawable(viewHolder.containerView.context, R.drawable.rect_round_primary_color)
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
                this.layoutParams = lParams
            }
        }
    }
}