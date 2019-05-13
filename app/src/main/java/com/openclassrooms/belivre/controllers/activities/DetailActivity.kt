package com.openclassrooms.belivre.controllers.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.Book
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.utils.GlideApp
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.BookViewModel
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var user: User

    private val bookVM: BookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { BookViewModel() }).get(BookViewModel::class.java)
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
    
    private fun updateUI(book: Book){
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
