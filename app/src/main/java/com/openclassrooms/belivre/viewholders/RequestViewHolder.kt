package com.openclassrooms.belivre.viewholders

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.GlideApp
import kotlinx.android.synthetic.main.request_item_row.view.*

/**
 * Viewholder used with UserBooks objects in LibraryActivity (BorrowedFragment)
 */
class RequestViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    private var view: View = v
    private var userBook : UserBook? = null

    fun bindUserBook(userBook: UserBook){
        this.userBook = userBook
        GlideApp.with(view.context)
            .load(userBook.coverUrl)
            .fitCenter()
            .error(R.mipmap.error)
            .into(view.coverRequest)
        view.titleRequest.text = userBook.title

        view.ownerRequestValue.text = userBook.userDisplayName

        view.cityRequestValue.text = userBook.cityName

        when(userBook.status){
            2 -> {
                view.statusRequestValue.text = view.context.getString(R.string.status_2)
                view.statusRequestValue.setTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
                view.alertICRequest.visibility = View.VISIBLE
                view.alertICRequest.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_info))
                view.alertICRequest.setColorFilter(ContextCompat.getColor(view.context, R.color.colorAccent))
            }
            4 -> {
                view.statusRequestValue.text = view.context.getString(R.string.status_4)
                view.statusRequestValue.setTextColor(Color.RED)
                view.alertICRequest.visibility = View.VISIBLE
                view.alertICRequest.setColorFilter(Color.RED)
            }
        }
    }
}