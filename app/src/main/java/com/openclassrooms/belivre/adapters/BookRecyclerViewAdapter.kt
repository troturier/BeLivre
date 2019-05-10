package com.openclassrooms.belivre.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.Book
import com.openclassrooms.belivre.utils.inflate
import com.openclassrooms.belivre.viewholders.BookViewholder

class BookRecyclerViewAdapter(private val books :MutableList<Book>, private val clickListener: (Book, Int) -> Unit): RecyclerView.Adapter<BookViewholder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewholder {
        val inflatedView = parent.inflate(R.layout.search_item_row, false)
        return BookViewholder(inflatedView)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: BookViewholder, position: Int) {
        val itemBook = books[position]
        holder.bindBook(itemBook)
        holder.itemView.setOnClickListener { clickListener(itemBook, position) }
    }
}