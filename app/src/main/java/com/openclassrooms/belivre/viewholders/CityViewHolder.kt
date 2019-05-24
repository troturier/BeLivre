package com.openclassrooms.belivre.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.GlideApp
import kotlinx.android.synthetic.main.city_item_row.view.*
import kotlinx.android.synthetic.main.fragment_list_item_row.view.*

class CityViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    private var view: View = v
    private var book: UserBook? = null

    fun bindUserBook(book: UserBook){
        this.book = book
        GlideApp.with(view.context)
            .load(book.coverUrl)
            .fitCenter()
            .error(R.mipmap.error)
            .into(view.coverListFragment)
        view.titleListFragment.text = book.title

        if (book.publisher != null) {
            view.publisherListFragmentValue.text = book.publisher
        } else {
            view.publisherListFragmentValue.text = view.context.getString(R.string.no_publisher)
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
            view.authorsListFragmentValue.text = authorsSTR
        } else {
            view.authorsListFragmentValue.text = view.context.getString(R.string.no_authors)
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
            view.categoriesListFragmentValue.text = categories
        } else {
            view.categoriesListFragmentValue.text = view.context.getString(R.string.no_categories)
        }

        view.ownerCityValue.text = book.userDisplayName
    }
}