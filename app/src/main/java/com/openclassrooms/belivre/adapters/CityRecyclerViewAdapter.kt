package com.openclassrooms.belivre.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.inflate
import com.openclassrooms.belivre.viewholders.CityViewHolder

/**
 * RecyclerViewAdapater for Cities objects of the City Activity
 */
class CityRecyclerViewAdapter(private val userBooks :List<UserBook>, private val clickListener: (UserBook, Int) -> Unit): RecyclerView.Adapter<CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val inflatedView = parent.inflate(R.layout.city_item_row, false)
        return CityViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return userBooks.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val itemUserBook = userBooks[position]
        holder.bindUserBook(itemUserBook)
        holder.itemView.setOnClickListener { clickListener(itemUserBook, position) }
    }
}