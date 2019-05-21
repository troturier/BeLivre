package com.openclassrooms.belivre.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.inflate
import com.openclassrooms.belivre.viewholders.RequestViewHolder

class RequestRecyclerViewAdapter (private val userBooks :List<UserBook>, private val clickListener: (UserBook, Int) -> Unit): RecyclerView.Adapter<RequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val inflatedView = parent.inflate(R.layout.request_item_row, false)
        return RequestViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return userBooks.size
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val itemUserBook = userBooks[position]
        holder.bindUserBook(itemUserBook)
        holder.itemView.setOnClickListener { clickListener(itemUserBook, position) }
    }
}