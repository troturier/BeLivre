package com.openclassrooms.belivre.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.BookReview
import com.openclassrooms.belivre.utils.GlideApp
import kotlinx.android.synthetic.main.detail_review_item_row.view.*

class ReviewViewholder (v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v
    private var review: BookReview? = null

    fun bindReview(review: BookReview){
        this.review = review

        val ref = FirebaseStorage.getInstance().reference.child("images/profilePictures/${review.userId.toString()}")

        when {
            review.profilePicUrl.equals("images/profilePictures/${review.userId.toString()}") -> GlideApp.with(view.context)
                .load(ref)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .fitCenter()
                .circleCrop()
                .into(view.userPicReview)
            review.profilePicUrl!!.isNotEmpty() -> GlideApp.with(view.context)
                .load(review.profilePicUrl)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .fitCenter()
                .circleCrop()
                .into(view.userPicReview)
            else -> {
                review.profilePicUrl = ""
                GlideApp.with(view.context)
                    .load(R.drawable.ic_avatar)
                    .signature(ObjectKey(System.currentTimeMillis().toString()))
                    .fitCenter()
                    .circleCrop()
                    .into(view.userPicReview)
            }
        }

        view.displayNameReview.text = review.displayName

        view.ratingBarReview.rating = review.rate!!.toFloat()

        if(review.content != null){
            view.contentReview.text = review.content
        }
    }
}