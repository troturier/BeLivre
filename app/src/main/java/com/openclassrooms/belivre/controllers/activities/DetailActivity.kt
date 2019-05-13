package com.openclassrooms.belivre.controllers.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.Book
import com.openclassrooms.belivre.models.BookReview
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.utils.GlideApp
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.BookReviewViewModel
import com.openclassrooms.belivre.viewmodels.BookViewModel
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.add_review_dialog.view.*

class DetailActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var book: Book
    private var review: BookReview? = null
    private var mMenu:Menu? = null

    private val bookVM: BookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { BookViewModel() }).get(BookViewModel::class.java)
    }

    private val reviewVM: BookReviewViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { BookReviewViewModel() }).get(BookReviewViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        user = intent.getSerializableExtra("user") as User
        
        bookVM.getBook(intent.getStringExtra("id")).observe(this, Observer { book:Book -> updateUI(book) })

        setSupportActionBar(toolbar_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
    
    private fun updateUI(bookP: Book){
        book = bookP

        //////////////////////////////
        // COVER
        //////////////////////////////

        GlideApp.with(this)
            .load(book.coverUrl)
            .fitCenter()
            .error(R.mipmap.error)
            .into(coverDetail)

        toolbar_detail.title = book.title

        //////////////////////////////
        // PUBLISHER
        //////////////////////////////

        if (book.publisher != null) {
            publisherDetail.text = getString(R.string.publisher_search, book.publisher)
        } else {
            publisherDetail.text = getString(R.string.no_publisher)
        }

        //////////////////////////////
        // PUBLISHED DATE
        //////////////////////////////

        if (book.publishedDate != null) {
            publishedOnDetail.text = getString(R.string.published_date, book.publishedDate)
        } else {
            publishedOnDetail.text = getString(R.string.no_published_date)
        }

        //////////////////////////////
        // AUTHORS
        //////////////////////////////

        if (book.authors != null) {
            var authorsSTR = ""
            for(a in book.authors!!){
                authorsSTR = if (authorsSTR.isEmpty()) {
                    authorsSTR + a
                } else {
                    getString(R.string.authors, authorsSTR, a)
                }
            }
            authorsDetail.text = getString(R.string.authors_search, authorsSTR)
        } else {
            authorsDetail.text = getString(R.string.no_authors)
        }

        //////////////////////////////
        // CATEGORIES
        //////////////////////////////

        if (book.categories != null) {
            var categories = ""
            for(c in book.categories!!){
                categories = if (categories.isEmpty()) {
                    categories + c
                } else {
                    getString(R.string.authors, categories, c)
                }
            }
            categoriesDetail.text = categories
        } else {
            categoriesDetail.text = getString(R.string.no_categories)
        }

        //////////////////////////////
        // DESCRIPTION
        //////////////////////////////

        if(book.description != null){
            descriptionDetail.text = book.description
        }
        else{
            descriptionDetail.text = getString(R.string.no_description)
        }

        reviewVM.getBookReview(book.id!!, book.id + user.id).observe(this, Observer { review:BookReview? -> updateUserReview(review) })
    }

    private fun updateUserReview(reviewP: BookReview?){
        review = reviewP
        if(review != null){
            mMenu!!.getItem(0).icon = ContextCompat.getDrawable(this, R.drawable.ic_star_review_full)
        }
    }

    @SuppressLint("InflateParams")
    private fun createReviewDialog() {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.add_review_dialog, null)

        if(review != null){
            dialogView.ratingBarReviewDialog.rating = review!!.rate!!.toFloat()
            if (review!!.content != null) dialogView.contentReviewDialog.setText(review!!.content)
        }

        val contentReview = dialogView.contentReviewDialog
        contentReview.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length == 500) {
                    dialogView.textCountReviewDialog.text =
                        getString(R.string.maxium_char_limit)
                    dialogView.textCountReviewDialog.setTextColor(Color.RED)
                } else {
                    dialogView.textCountReviewDialog.text = getString(R.string.count_char, s.length.toString())
                    dialogView.textCountReviewDialog.setTextColor(
                        ContextCompat.getColor(
                            dialogView.context,
                            R.color.default_text_color
                        )
                    )
                }
            }
        })

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Send", null)
            .setNeutralButton("Cancel") { _, _ -> }

        val dialog = alertDialog.create()

        dialog.setOnShowListener {
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener {
                val bookReview = BookReview(
                    book.id + user.id,
                    user.id, book.id,
                    getString(R.string.profile_display_name, user.firstname, user.lastname?.substring(0,1)),
                    user.profilePicURL,
                    String.format("%.1f",dialogView.ratingBarReviewDialog.rating).toDouble(),
                    dialogView.contentReviewDialog.text.toString())
                reviewVM.addBookReview(bookReview)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        mMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.write_review_button -> createReviewDialog()
            R.id.add_to_wishlist -> toast("Wishlist")
            else -> return false
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, DetailActivity::class.java)
        }
    }
}
