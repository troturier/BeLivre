package com.openclassrooms.belivre.controllers.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.UserBookRecyclerViewAdapter
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import kotlinx.android.synthetic.main.fragment_borrowed.*

/**
 * A simple [Fragment] subclass.
 */
class BorrowedFragment : Fragment() {

    private lateinit var linearLayoutManagerBorrowed: LinearLayoutManager
    private lateinit var linearLayoutManagerRequest: LinearLayoutManager
    private lateinit var adapterBorrowed: UserBookRecyclerViewAdapter
    private lateinit var adapterRequest: UserBookRecyclerViewAdapter
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
        borrrowedRVBorrowedFragment.layoutManager = linearLayoutManagerBorrowed

        linearLayoutManagerRequest = LinearLayoutManager(activity)
        requestRVBorrowedFragment.layoutManager = linearLayoutManagerRequest


    }
}