package com.openclassrooms.belivre.viewholders

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.GlideApp
import kotlinx.android.synthetic.main.mybooks_item_row.view.*

class UserBookViewHolder (v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
    private var view:View = v
    private var userBook : UserBook? = null

    init {
        v.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        Log.d("RecyclerView", "CLICK!")
    }

    fun bindUserBook(userBook: UserBook){
        this.userBook = userBook
        GlideApp.with(view.context)
            .load(userBook.coverUrl)
            .fitCenter()
            .error(R.mipmap.error)
            .into(view.coverMyBooks)
        view.titleMyBooks.text = userBook.title
        if (userBook.publisher != null) {
            view.publisherMyBooks.text = view.context.getString(R.string.publisher_search, userBook.publisher)
        } else {
            view.publisherMyBooks.text = view.context.getString(R.string.no_publisher)
        }

        if (userBook.authors != null) {
            var authorsSTR = ""
            for(a in userBook.authors!!){
                authorsSTR = if (authorsSTR.isEmpty()) {
                    authorsSTR + a
                } else {
                    view.context.getString(R.string.authors, authorsSTR, a)
                }
            }
            view.authorsMyBooks.text = view.context.getString(R.string.authors_search, authorsSTR)
        } else {
            view.authorsMyBooks.text = view.context.getString(R.string.no_authors)
        }

        if (userBook.categories != null) {
            var categories = ""
            for(c in userBook.categories!!){
                categories = if (categories.isEmpty()) {
                    categories + c
                } else {
                    view.context.getString(R.string.authors, categories, c)
                }
            }
            view.categoriesMyBooks.text = view.context.getString(R.string.categories_search, categories)
        } else {
            view.categoriesMyBooks.text = view.context.getString(R.string.no_categories)
        }
    }
}