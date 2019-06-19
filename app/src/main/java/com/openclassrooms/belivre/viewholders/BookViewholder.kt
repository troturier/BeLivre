package com.openclassrooms.belivre.viewholders

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.Book
import com.openclassrooms.belivre.utils.GlideApp
import kotlinx.android.synthetic.main.search_item_row.view.*

/**
 * Viewholder used with Books objects in SearchActivity
 */
class BookViewholder (v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    private var view: View = v
    private var book:Book? = null

    init {
        v.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        Log.d("RecyclerView", "CLICK!")
    }

    fun bindBook(book: Book){
        this.book = book
        GlideApp.with(view.context)
            .load(book.coverUrl)
            .fitCenter()
            .error(R.mipmap.error)
            .into(view.coverSearch)
        view.titleSearch.text = book.title

        if (book.publisher != null) {
            view.publisherSearch.text = view.context.getString(R.string.publisher_search, book.publisher)
        } else {
            view.publisherSearch.text = view.context.getString(R.string.no_publisher)
        }

        if (book.authors != null) {
            var authorsSTR = ""
            for(a in book.authors!!){
                authorsSTR = if (authorsSTR.isEmpty()) {
                    authorsSTR + a
                } else {
                    view.context.getString(R.string.authors, authorsSTR, a)
                }
            }
            view.authorsSearch.text = view.context.getString(R.string.authors_search, authorsSTR)
        } else {
            view.authorsSearch.text = view.context.getString(R.string.no_authors)
        }

        if (book.categories != null) {
            var categories = ""
            for(c in book.categories!!){
                categories = if (categories.isEmpty()) {
                    categories + c
                } else {
                    view.context.getString(R.string.authors, categories, c)
                }
            }
            view.categoriesSearch.text = view.context.getString(R.string.categories_search, categories)
        } else {
            view.categoriesSearch.text = view.context.getString(R.string.no_categories)
        }
    }
}