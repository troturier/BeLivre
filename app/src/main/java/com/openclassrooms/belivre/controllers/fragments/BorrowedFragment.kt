package com.openclassrooms.belivre.controllers.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.BorrowedRecyclerViewAdapter
import com.openclassrooms.belivre.adapters.RequestRecyclerViewAdapter
import com.openclassrooms.belivre.controllers.activities.LibraryActivity
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import kotlinx.android.synthetic.main.fragment_borrowed.*

/**
 * A simple [Fragment] subclass.
 */
class BorrowedFragment : Fragment() {

    private lateinit var linearLayoutManagerBorrowed: LinearLayoutManager
    private lateinit var linearLayoutManagerRequest: LinearLayoutManager
    private lateinit var adapterBorrowed: BorrowedRecyclerViewAdapter
    private lateinit var adapterRequest: RequestRecyclerViewAdapter
    private var currentUser: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null

    private val userBookVM: UserBookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserBookViewModel() }).get(UserBookViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_borrowed, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.library_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth?.currentUser

        linearLayoutManagerBorrowed = LinearLayoutManager(activity)
        borrowedRVBorrowedFragment.layoutManager = linearLayoutManagerBorrowed

        linearLayoutManagerRequest = LinearLayoutManager(activity)
        requestRVBorrowedFragment.layoutManager = linearLayoutManagerRequest

        userBookVM.getRequestUserBooksByRequestSenderId(LibraryActivity.user.id.toString()).observe(this, Observer{
                userBooks:List<UserBook>? -> configureBorrowedRecyclerView(userBooks)})
    }

    private fun configureBorrowedRecyclerView(userBooks:List<UserBook>?){
        if (userBooks != null) {
            val userBooksBorrowed:MutableList<UserBook>? = mutableListOf()
            val userBooksRequest:MutableList<UserBook>? = mutableListOf()

            for(ub in userBooks){
                when(ub.status){
                    3 -> userBooksBorrowed!!.add(ub)
                    2 -> if(ub.requestSenderId == LibraryActivity.user.id)userBooksRequest!!.add(ub)
                    4 -> userBooksRequest!!.add(ub)
                }
            }

            val sortedUserBooksBorrowed = userBooksBorrowed!!.sortedWith(compareBy { it.title })
            adapterBorrowed = BorrowedRecyclerViewAdapter(sortedUserBooksBorrowed) { item: UserBook, _: Int ->
                activity!!.toast("BORROWED")
            }
            borrowedRVBorrowedFragment.adapter = adapterBorrowed
            adapterBorrowed.notifyDataSetChanged()


            val sortedUserBooksRequest = userBooksRequest!!.sortedWith(compareBy { it.title })
            adapterRequest = RequestRecyclerViewAdapter(sortedUserBooksRequest) { item: UserBook, _: Int ->
                activity!!.toast("Request")
            }
            requestRVBorrowedFragment.adapter = adapterRequest
            adapterRequest.notifyDataSetChanged()
        }
    }
}