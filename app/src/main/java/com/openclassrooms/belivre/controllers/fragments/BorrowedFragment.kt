package com.openclassrooms.belivre.controllers.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.openclassrooms.belivre.R

/**
 * A simple [Fragment] subclass.
 */
class BorrowedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_borrowed, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.library_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}