package com.openclassrooms.belivre.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.ListRecyclerViewAdapter
import com.openclassrooms.belivre.models.Book
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.BookViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() , LifecycleOwner{

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: ListRecyclerViewAdapter

    private val bookVM: BookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { BookViewModel() }).get(BookViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(activity)
        listRV_main.layoutManager = linearLayoutManager

        bookVM.getBooks().observe(this, Observer { books:List<Book>? -> configureRecyclerView(books)})
    }

    private fun configureRecyclerView(books: List<Book>?){
        if(books != null){
            val sortedBooks = books.sortedWith(compareBy{ it.title })
            adapter = ListRecyclerViewAdapter(sortedBooks){ item: Book, _: Int ->
                activity!!.toast(item.title.toString())
            }
            listRV_main.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}
