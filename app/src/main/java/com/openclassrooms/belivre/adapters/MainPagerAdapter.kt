package com.openclassrooms.belivre.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.controllers.fragments.ChatFragment
import com.openclassrooms.belivre.controllers.fragments.ListFragment
import com.openclassrooms.belivre.controllers.fragments.MapFragment

/**
 * PagerAdapater for the Main Activity
 */
class MainPagerAdapter (fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                MapFragment()
            }
            1 -> {
                ListFragment()
            }
            else -> {
                return ChatFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> R.string.map.toString()
            1 -> {
                R.string.list.toString()
            }
            else -> {
                return R.string.chat.toString()
            }
        }
    }
}