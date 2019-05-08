package com.openclassrooms.belivre.controllers.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment


/**
 * A simple [Fragment] subclass.
 */
class MyBooksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(com.openclassrooms.belivre.R.layout.fragment_mybooks, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(com.openclassrooms.belivre.R.menu.library_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}