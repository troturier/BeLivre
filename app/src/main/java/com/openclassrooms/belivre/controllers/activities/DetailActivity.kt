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
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.ReviewsRecyclerViewAdapter
import com.openclassrooms.belivre.models.*
import com.openclassrooms.belivre.utils.GlideApp
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.*
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.add_review_dialog.view.*

class DetailActivity : AppCompatActivity() {

    // DATA
    private lateinit var user: User
    private lateinit var book: Book
    private var rateSum: Double = 0.0
    private var review: BookReview? = null

    // UI
    private var mMenu:Menu? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ReviewsRecyclerViewAdapter

    // VIEW MODELS
    private val bookVM: BookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { BookViewModel() }).get(BookViewModel::class.java)
    }

    private val reviewVM: BookReviewViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { BookReviewViewModel() }).get(BookReviewViewModel::class.java)
    }

    private val userBookVM: UserBookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserBookViewModel() }).get(UserBookViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Retrieving current user's data from intent
        user = intent.getSerializableExtra("user") as User

        // Retrieving selected book's data from Firestore
        bookVM.getBook(intent.getStringExtra("id")).observe(this, Observer { book:Book -> updateUI(book) })

        // REVIEW RECYCLER VIEW
        linearLayoutManager = LinearLayoutManager(this)
        reviewsRecyclerView.layoutManager = linearLayoutManager

        setSupportActionBar(toolbar_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    /**
     * Updates UI with retrieved data
     * @param bookP Book - Book object retrieved from Firestore
     */
    private fun updateUI(bookP: Book){
        book = bookP

        userBookVM.getAvailableUserBooksByBookId(book.id.toString()).observe(this, Observer { userBooks:List<UserBook>? -> configureOffersButton(userBooks) })

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
            if(book.publishedDate.toString().contains("T", false)){
                val dateSTR = book.publishedDate.toString().split("T")
                publishedOnDetail.text = getString(R.string.published_date,dateSTR[0])
            }
            else publishedOnDetail.text = getString(R.string.published_date, book.publishedDate)
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
            if(descriptionDetail.lineCount <= 10){
                expandButtonLLDetail.visibility = View.GONE
            }
            expandButtonDetail.setOnClickListener {
                if(descriptionDetail.isExpanded) expandButtonDetail.text = getString(R.string.show_more)
                else expandButtonDetail.text = getString(R.string.show_less)
                descriptionDetail.toggle()
            }
        }
        else{
            descriptionDetail.text = getString(R.string.no_description)
        }

        //////////////////////////////
        // RATING
        //////////////////////////////

        rateSum = book.rating
        ratingBarDetail.rating = rateSum.toFloat()

        reviewVM.getBookReview(book.id!!, book.id + user.id).observe(this, Observer { review:BookReview? -> updateUserReview(review) })
    }

    /**
     * Start OffersActivity with required data
     * @param userbooks List<UserBook>?
     */
    private fun configureOffersButton(userbooks:List<UserBook>?){
        val sortedList:MutableList<UserBook>? = mutableListOf()
        if (userbooks != null) {
            for(ub in userbooks) {
                if (ub.userId.toString() != user.id.toString()) {
                    sortedList!!.add(ub)
                }
            }
            offersDetail.setOnClickListener {
                if (sortedList!!.size > 0) {
                    val intent = OffersActivity.newIntent(this)
                    intent.putExtra("book", book)
                    intent.putExtra("user", user)
                    startActivity(intent)
                } else {
                    toast("Sorry, there is no offer for this book at the moment")
                }
            }
            offersDetail.text = getString(R.string.offers_count, sortedList!!.size.toString())
        } else {
            offersDetail.text = getString(R.string.offers_count, sortedList!!.size.toString())
        }
    }

    /**
     * Update the current user's review of the book
     * @param reviewP BookReview?
     */
    private fun updateUserReview(reviewP: BookReview?){
        review = reviewP
        if(review != null){
            mMenu!!.getItem(0).icon = ContextCompat.getDrawable(this, R.drawable.ic_star_review_full)
        }
        reviewVM.getBookReviews(book.id.toString()).observe(this, Observer { reviews:List<BookReview>? -> configureRecyclerView(reviews) })
    }

    /**
     * Update rating summary of the book
     * @param reviews List<BookReview>?
     */
    private fun updateRateSum(reviews: List<BookReview>?){
        if (reviews != null && reviews.isNotEmpty()) {
            rateSum = 0.0
            for(r in reviews){
                rateSum += r.rate!!
            }
            rateSum = rateSum.div(reviews.size)
            book.rating = rateSum
            bookVM.updateBookRating(book)
        }
        else{
            book.rating = 0.0
            bookVM.updateBookRating(book)
            adapter = ReviewsRecyclerViewAdapter(reviews)
            reviewsRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * Configure the RecyclerView
     * @param reviews List<BookReview>?
     */
    private fun configureRecyclerView(reviews:List<BookReview>?){
        if(reviews != null && reviews.isNotEmpty()){
            reviewLLDetail.visibility = View.VISIBLE

            adapter = ReviewsRecyclerViewAdapter(reviews)
            reviewsRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

            reviewCountDetail.text = getString(R.string.reviewCount, reviews.size.toString())
        }
        else{
            reviewCountDetail.text = getString(R.string.reviewCount, "0")
            reviewLLDetail.visibility = View.GONE
        }
    }

    /**
     * Displays the Review Dialog
     * Allows the current user to add/update/remove a review
     */
    @SuppressLint("InflateParams")
    private fun createReviewDialog() {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.add_review_dialog, null)

        // CHECK IF A REVIEW ALREADY EXIST
        if(review != null){
            dialogView.ratingBarReviewDialog.rating = review!!.rate!!.toFloat()
            if (review!!.content != null){
                dialogView.contentReviewDialog.setText(review!!.content)
                dialogView.textCountReviewDialog.text = getString(R.string.count_char, review!!.content!!.length.toString())
            }
        }

        // REVIEW TEXT
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
            .setNegativeButton("Delete", null)

        val dialog = alertDialog.create()

        dialog.setOnShowListener {
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener {
                var rateSTR = ("%.1f".format(dialogView.ratingBarReviewDialog.rating))
                if(rateSTR.contains(",")) rateSTR = rateSTR.replace(",",".")
                val rateDouble = rateSTR.toDouble()

                val bookReview = BookReview(
                    book.id + user.id,
                    user.id, book.id,
                    getString(R.string.profile_display_name, user.firstname, user.lastname?.substring(0,1)),
                    user.profilePicURL,
                    rateDouble,
                    dialogView.contentReviewDialog.text.toString())
                reviewVM.addBookReview(bookReview)

                reviewVM.getBookReviews(book.id.toString()).observe(this, Observer { reviews: List<BookReview>? -> updateRateSum(reviews)})

                dialog.dismiss()
            }

            val deleteButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            deleteButton.setOnClickListener{
                val bookReview = BookReview(book.id + user.id, user.id, book.id)
                reviewVM.deleteBookReview(bookReview)
                reviewVM.getBookReviews(book.id.toString()).observe(this, Observer { reviews: List<BookReview>? -> updateRateSum(reviews)})
                dialog.dismiss()
                mMenu!!.getItem(0).icon = ContextCompat.getDrawable(this, R.drawable.ic_star_review)
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
