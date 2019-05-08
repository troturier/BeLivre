package com.openclassrooms.belivre.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.openclassrooms.belivre.controllers.fragments.BorrowedFragment
import com.openclassrooms.belivre.controllers.fragments.MyBooksFragment

class LibraryPagerAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                MyBooksFragment()
            }
            else -> {
                return BorrowedFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "My Books"
            else -> {
                return "Borrowed"
            }
        }
    }
}