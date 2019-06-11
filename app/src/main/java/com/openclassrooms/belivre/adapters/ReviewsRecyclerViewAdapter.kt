package com.openclassrooms.belivre.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.BookReview
import com.openclassrooms.belivre.utils.inflate
import com.openclassrooms.belivre.viewholders.ReviewViewholder

/**
 * RecyclerViewAdapater for Reviews objects of the Detail Activity
 */
class ReviewsRecyclerViewAdapter(private val reviews :List<BookReview>?): RecyclerView.Adapter<ReviewViewholder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewholder {
        val inflatedView = parent.inflate(R.layout.detail_review_item_row, false)
        return ReviewViewholder(inflatedView)
    }

    override fun getItemCount(): Int {
        return if (reviews.isNullOrEmpty()) 0
        else reviews.size
    }

    override fun onBindViewHolder(holder: ReviewViewholder, position: Int) {
        val itemReview = this.reviews!![position]
        holder.bindReview(itemReview)
    }
}