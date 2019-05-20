package com.openclassrooms.belivre.viewholders

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
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

        when(userBook.status){
            1 -> {
                view.statusMyBooksValue.text = view.context.getString(R.string.status_1)
                view.statusMyBooksValue.setTypeface(null, Typeface.BOLD)
                view.statusMyBooksValue.setTextColor(ContextCompat.getColor(view.context, R.color.colorPrimaryDark))
                view.alertICMyBooks.visibility = View.VISIBLE
                view.alertICMyBooks.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_smiley))
                view.alertICMyBooks.setColorFilter(ContextCompat.getColor(view.context, R.color.colorPrimaryDark))
            }
            2 -> {
                view.statusMyBooksValue.text = view.context.getString(R.string.status_2)
                view.statusMyBooksValue.setTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
                view.alertICMyBooks.visibility = View.VISIBLE
                view.alertICMyBooks.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_info))
                view.alertICMyBooks.setColorFilter(ContextCompat.getColor(view.context, R.color.colorAccent))
            }
            3 -> {
                view.statusMyBooksValue.text = view.context.getString(R.string.status_3)
                view.statusMyBooksValue.setTextColor(ContextCompat.getColor(view.context, R.color.colorPrimary))
                view.alertICMyBooks.visibility = View.VISIBLE
                view.statusMyBooksValue.setTypeface(null, Typeface.ITALIC)
                view.alertICMyBooks.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_time))
                view.alertICMyBooks.setColorFilter(ContextCompat.getColor(view.context, R.color.colorPrimary))
            }
            4 -> {
                view.statusMyBooksValue.text = view.context.getString(R.string.status_4)
                view.statusMyBooksValue.setTextColor(Color.RED)
                view.alertICMyBooks.visibility = View.VISIBLE
                view.alertICMyBooks.setColorFilter(Color.RED)
            }
        }

        if(userBook.lastBorrowerId != null){
            view.lastBorrowerMyBooksValue.text = userBook.lastBorrowerDisplayName
        }
        else{
            view.lastBorrowerMyBooksValue.text = view.context.getString(R.string.not_borrowed_yet)
        }

        if(userBook.lastBorrowedOn != null){
            view.borrowedDateMyBooksValue.text =userBook.lastBorrowedOn
        }
        else{
            view.borrowedDateMyBooksValue.text = view.context.getString(R.string.not_borrowed_yet)
        }
    }
}