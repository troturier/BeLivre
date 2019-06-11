package com.openclassrooms.belivre.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.openclassrooms.belivre.controllers.fragments.BorrowedFragment
import com.openclassrooms.belivre.controllers.fragments.MyBooksFragment

/**
 * PagerAdapter for the Library Activity
 */
class LibraryPagerAdapter (fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

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