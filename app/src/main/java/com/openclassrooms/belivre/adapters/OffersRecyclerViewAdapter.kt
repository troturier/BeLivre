package com.openclassrooms.belivre.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.inflate
import com.openclassrooms.belivre.viewholders.OffersViewHolder

class OffersRecyclerViewAdapter (private val userBooks :List<UserBook>, private val clickListener: (UserBook, Int) -> Unit): RecyclerView.Adapter<OffersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffersViewHolder {
        val inflatedView = parent.inflate(R.layout.offers_item_row, false)
        return OffersViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return userBooks.size
    }

    override fun onBindViewHolder(holder: OffersViewHolder, position: Int) {
        val itemUserBook = userBooks[position]
        holder.bindUserBook(itemUserBook)
        holder.itemView.setOnClickListener { clickListener(itemUserBook, position) }
    }
}