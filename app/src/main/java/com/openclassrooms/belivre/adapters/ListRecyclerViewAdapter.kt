package com.openclassrooms.belivre.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.Book
import com.openclassrooms.belivre.utils.inflate
import com.openclassrooms.belivre.viewholders.BookListViewHolder

/**
 * RecyclerViewAdapater for Books objects for the List tab of the Main Activity
 */
class ListRecyclerViewAdapter(private val books :List<Book>, private val clickListener: (Book, Int) -> Unit): RecyclerView.Adapter<BookListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val inflatedView = parent.inflate(R.layout.fragment_list_item_row, false)
        return BookListViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        val itemBook = books[position]
        holder.bindBook(itemBook)
        holder.itemView.setOnClickListener { clickListener(itemBook, position) }
    }
}