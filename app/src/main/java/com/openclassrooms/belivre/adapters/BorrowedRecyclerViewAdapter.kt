package com.openclassrooms.belivre.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.inflate
import com.openclassrooms.belivre.viewholders.BorrowedViewHolder

class BorrowedRecyclerViewAdapter (private val userBooks :List<UserBook>, private val clickListener: (UserBook, Int) -> Unit): RecyclerView.Adapter<BorrowedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowedViewHolder {
        val inflatedView = parent.inflate(R.layout.borrowed_item_row, false)
        return BorrowedViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return userBooks.size
    }

    override fun onBindViewHolder(holder: BorrowedViewHolder, position: Int) {
        val itemUserBook = userBooks[position]
        holder.bindUserBook(itemUserBook)
        holder.itemView.setOnClickListener { clickListener(itemUserBook, position) }
    }
}