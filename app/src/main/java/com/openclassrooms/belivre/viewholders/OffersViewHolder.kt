package com.openclassrooms.belivre.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import kotlinx.android.synthetic.main.offers_item_row.view.*

class OffersViewHolder (v: View) : RecyclerView.ViewHolder(v) {
    private var view:View = v
    private var userBook : UserBook? = null

    fun bindUserBook(userBook: UserBook){
        this.userBook = userBook

        view.userDNOffers.text = userBook.userDisplayName

        loadProfilePictureIntoImageView(view.userPicOffers, view.context, userBook.userPicUrl, userBook.userId.toString())

        view.cityOffersValue.text = userBook.cityName
    }
}