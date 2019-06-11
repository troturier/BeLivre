package com.openclassrooms.belivre.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.inflate
import com.openclassrooms.belivre.viewholders.UserBookViewHolder

/**
 * RecyclerViewAdapater for UserBooks objects of Library Activity's "MyBooks" tab
 */
class UserBookRecyclerViewAdapter(private val userBooks :List<UserBook>, private val clickListener: (UserBook, Int) -> Unit): RecyclerView.Adapter<UserBookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBookViewHolder {
        val inflatedView = parent.inflate(R.layout.mybooks_item_row, false)
        return UserBookViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return userBooks.size
    }

    override fun onBindViewHolder(holder: UserBookViewHolder, position: Int) {
        val itemUserBook = userBooks[position]
        holder.bindUserBook(itemUserBook)
        holder.itemView.setOnClickListener { clickListener(itemUserBook, position) }
    }
}