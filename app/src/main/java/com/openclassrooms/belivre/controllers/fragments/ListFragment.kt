package com.openclassrooms.belivre.controllers.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.ListRecyclerViewAdapter
import com.openclassrooms.belivre.controllers.activities.DetailActivity
import com.openclassrooms.belivre.controllers.activities.MainActivity
import com.openclassrooms.belivre.models.Book
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.BookViewModel
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.search_dialog.view.*

/**
 * Fragment used to display all books stored in Firestore in a list
 * @property linearLayoutManager LinearLayoutManager
 * @property adapter ListRecyclerViewAdapter
 * @property categoriesSet MutableSet<String>?
 * @property books List<Book>?
 * @property bookVM BookViewModel
 */
class ListFragment : Fragment() , LifecycleOwner{

    // UI
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ListRecyclerViewAdapter
    // DATA
    private var categoriesSet: MutableSet<String>? = null
    private var books : List<Book>? = null

    // VIEW MODEL
    private val bookVM: BookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { BookViewModel() }).get(BookViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuring the RecyclerView
        linearLayoutManager = LinearLayoutManager(activity)
        listRV_main.layoutManager = linearLayoutManager
        bookVM.getBooks().observe(this, Observer { books:List<Book>? -> configureRecyclerView(books); this.books = books})
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Configure the RecyclerView with the retrieved book list
     * And retrieving used categories into a sorted list
     * @param books List<Book>? - Books retrieved from Firestore
     */
    private fun configureRecyclerView(books: List<Book>?){
        if(books != null){
            // Sorting books by their name
            val sortedBooks = books.sortedWith(compareBy{ it.title })

            // Retrieving all categories in a list
            val categories = mutableListOf<String>()
            for(book in sortedBooks){
                if (!book.categories.isNullOrEmpty()) {
                    for(cat in book.categories!!){
                        categories.add(cat)
                    }
                }
            }
            // Removing duplicated categories
            categoriesSet = categories.toMutableSet()

            // Configuring adapter + onClickListener
            adapter = ListRecyclerViewAdapter(sortedBooks){ item: Book, _: Int ->
                val intent = DetailActivity.newIntent(activity!!.applicationContext)
                intent.putExtra("id", item.id)
                intent.putExtra("user", MainActivity.user)
                startActivity(intent)
            }
            listRV_main.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * Updating the RecyclerView with new results
     * @param books List<Book>? - Books retrieved from Firestore
     */
    private fun updateRecyclerView(books:List<Book>?){
        if(books != null){
            val sortedBooks = books.sortedWith(compareBy{ it.title })
            adapter = ListRecyclerViewAdapter(sortedBooks){ item: Book, _: Int ->
                val intent = DetailActivity.newIntent(activity!!.applicationContext)
                intent.putExtra("id", item.id)
                intent.putExtra("user", MainActivity.user)
                startActivity(intent)
            }
            listRV_main.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * Display a Search Dialog
     */
    @SuppressLint("InflateParams")
    private fun showSearchDialog(){
        val layoutInflater = this.layoutInflater
        val dialogView = layoutInflater.inflate(R.layout.search_dialog, null)

        // Minimal and maximal values for rating comparaison
        var minRating = 0.0
        var maxRating = 0.0

        // List of checked/selected categories
        val selectedCategories = mutableListOf<String>()

        // Adding a new checkbox for each category
        if(!categoriesSet.isNullOrEmpty()){
            for(cat in categoriesSet!!){
                val checkBox = CheckBox(dialogView.context)
                checkBox.text = cat
                checkBox.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                // Adding/removing categories to selectedCategories when a checkbox is checked/removed
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked) selectedCategories.add(cat)
                    else selectedCategories.remove(cat)
                }
                dialogView.categoriesCBListLLSearchDialog.addView(checkBox)
            }
        }

        // Configuring the double seekbar of the Search Dialog
        dialogView.rangeSeekbarSearchDialog.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            dialogView.minSBsearchDialog.text = minValue.toString()
            minRating = minValue.toDouble()
            dialogView.maxSBsearchDialog.text = maxValue.toString()
            maxRating = maxValue.toDouble()
        }

        val alertDialog = AlertDialog.Builder(this.activity)
            .setView(dialogView)
        val dialog = alertDialog.create()

        // Action of the Search button
        dialogView.searchButtonSearchDialog.setOnClickListener {
            val booksFiltered : MutableList<Book> = mutableListOf()
            val booksList: List<Book>?
            for(book in this.books!!){
                var found = true

                // TITLE FIELD
                if(!dialogView.titleSearchDialog.text.isNullOrEmpty()){
                    found = book.title!!.toLowerCase().contains(dialogView.titleSearchDialog.text.toString().toLowerCase())
                }

                // AUTHORS FIELD
                if(!dialogView.authorsSearchDialog.text.isNullOrEmpty()){
                    var authorFound = false
                    for(author in book.authors!!){
                        if(author.toLowerCase().contains(dialogView.authorsSearchDialog.text.toString().toLowerCase())){
                            authorFound = true
                        }
                    }
                    found = authorFound
                }

                // PUBLISHERS FIELD
                if(!dialogView.publishersSearchDialog.text.isNullOrEmpty()){
                    found = book.publisher!!.toLowerCase().contains(dialogView.publishersSearchDialog.text.toString().toLowerCase())
                }

                // CATEGORIES CHECKBOX
                if(!selectedCategories.isNullOrEmpty() && !book.categories!!.isNullOrEmpty()){
                    found = book.categories!!.intersect(selectedCategories).isNotEmpty()
                }

                // RATING SEEKBAR
                val ratingFound = book.rating in minRating..maxRating

                if(found && ratingFound) booksFiltered.add(book)
            }

            booksList = booksFiltered

            // NO BOOK HAS BEEN FOUND
            if(booksList.isNullOrEmpty() && (!dialogView.titleSearchDialog.text.isNullOrEmpty() || !dialogView.authorsSearchDialog.text.isNullOrEmpty() || !dialogView.publishersSearchDialog.text.isNullOrEmpty())) {
                this.activity!!.toast("Sorry, no books match these criteria")
            }
            // NO FIELD HAS BEEN FILLED
            else if (booksList.isNullOrEmpty() && dialogView.titleSearchDialog.text.isNullOrEmpty() && dialogView.authorsSearchDialog.text.isNullOrEmpty() && dialogView.publishersSearchDialog.text.isNullOrEmpty()){
                updateRecyclerView(this.books)
                dialog.dismiss()
            }
            // AT LEAST ONE FIELD/CHECKBOX HAS BEEN FILLED
            else {
                updateRecyclerView(booksList)
                dialog.dismiss()
            }
        }

        // CANCEL BUTTON ACTION
        dialogView.cancelButtonSearchDialog.setOnClickListener {
            updateRecyclerView(this.books)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.search_icon) {
            showSearchDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
