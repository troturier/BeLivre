package com.openclassrooms.belivre.chat.recyclerViewItems

import android.content.Context
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.chat.TextMessage
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*

/**
 * Adapter class for Messages
 * Extends from MessageItem
 * @property message TextMessage
 * @property context Context
 * @constructor
 */
class TextMessageItem(val message: TextMessage,
                      val context: Context)
    : MessageItem(message) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_message_text.text = message.text
        super.bind(viewHolder, position)
    }

    override fun getLayout() = R.layout.item_text_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is TextMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
       return isSameAs(other as? TextMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

}