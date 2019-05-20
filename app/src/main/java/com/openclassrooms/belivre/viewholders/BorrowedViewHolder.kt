package com.openclassrooms.belivre.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.GlideApp
import kotlinx.android.synthetic.main.borrowed_item_row.view.*

class BorrowedViewHolder (v: View) : RecyclerView.ViewHolder(v) {
    private var view: View = v
    private var userBook : UserBook? = null

    fun bindUserBook(userBook: UserBook){
        this.userBook = userBook
        GlideApp.with(view.context)
            .load(userBook.coverUrl)
            .fitCenter()
            .error(R.mipmap.error)
            .into(view.coverBorrowed)
        view.titleBorrowed.text = userBook.title

        view.ownerBorrowedValue.text = userBook.userDisplayName

        view.cityBorrowedValue.text = userBook.cityName

        view.borrowedDateBorrowedValue.text = userBook.lastBorrowedOn
    }
}