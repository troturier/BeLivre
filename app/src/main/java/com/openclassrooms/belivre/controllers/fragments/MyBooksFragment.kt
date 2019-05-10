package com.openclassrooms.belivre.controllers.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.api.getBooks
import com.openclassrooms.belivre.controllers.activities.SearchActivity
import com.openclassrooms.belivre.models.Book
import com.openclassrooms.belivre.models.apiModels.BookResults
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.BookViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class MyBooksFragment : Fragment() {

    private val bookVM: BookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { BookViewModel() }).get(BookViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(com.openclassrooms.belivre.R.layout.fragment_mybooks, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(com.openclassrooms.belivre.R.menu.library_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.plus_icon) {
            val intent = Intent(activity, SearchActivity::class.java)
            this.startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}