package com.openclassrooms.belivre.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.models.BookReview
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import kotlinx.android.synthetic.main.detail_review_item_row.view.*

class ReviewViewholder (v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    private var review: BookReview? = null

    fun bindReview(review: BookReview){
        this.review = review

        loadProfilePictureIntoImageView(view.userPicReview, view.context, review.profilePicUrl, review.userId.toString())

        view.displayNameReview.text = review.displayName

        view.ratingBarReview.rating = review.rate!!.toFloat()

        if(review.content != null){
            view.contentReview.text = review.content
        }
    }
}