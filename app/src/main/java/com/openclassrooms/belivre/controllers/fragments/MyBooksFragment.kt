package com.openclassrooms.belivre.controllers.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.UserBookRecyclerViewAdapter
import com.openclassrooms.belivre.controllers.activities.DetailActivity
import com.openclassrooms.belivre.controllers.activities.LibraryActivity
import com.openclassrooms.belivre.controllers.activities.SearchActivity
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import kotlinx.android.synthetic.main.fragment_mybooks.*


/**
 * A simple [Fragment] subclass.
 */
class MyBooksFragment : Fragment(), LifecycleOwner {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: UserBookRecyclerViewAdapter
    private var currentUser: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null

    private val userBookVM: UserBookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserBookViewModel() }).get(UserBookViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mybooks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth?.currentUser

        linearLayoutManager = LinearLayoutManager(activity)
        mybooksRV.layoutManager = linearLayoutManager

        userBookVM.getUserBooksByUserId(currentUser!!.uid).observe(this, Observer { userBooks:List<UserBook>? -> configureRecyclerView(userBooks)})

    }

    private fun configureRecyclerView(userBooks:List<UserBook>?) {
        if (userBooks != null) {
            val sortedUserBooks = userBooks.sortedWith(compareBy { it.title })
            adapter = UserBookRecyclerViewAdapter(sortedUserBooks) { item: UserBook, _: Int ->

                val alertDialog = AlertDialog.Builder(this.activity!!)
                    .setTitle(item.title)
                    .setMessage(getString(R.string.what_do_you_want))
                    .setPositiveButton(getString(R.string.view_details)){ dialog, _ ->
                        val intent = DetailActivity.newIntent(activity!!.applicationContext)
                        intent.putExtra("id", item.bookId)
                        intent.putExtra("user", LibraryActivity.user)
                        startActivity(intent)
                        dialog.dismiss()
                    }
                    .setNeutralButton(getString(R.string.cancel)) { _, _ -> }

                if (item.status == 1) {
                    alertDialog.setNegativeButton(getString(R.string.delete)){ dialog, _ ->
                        val alertDialogC = AlertDialog.Builder(this.activity!!)
                            .setTitle(getString(R.string.delete))
                            .setMessage(getString(R.string.are_you_sure))
                            .setPositiveButton(getString(R.string.yes)) { dialogCS, _ ->
                                userBookVM.deleteUserBook(item)
                                dialogCS.dismiss()
                                dialog.dismiss()
                            }
                            .setNegativeButton(getString(R.string.no)) { dialogCNS, _ ->
                                dialogCNS.dismiss()
                            }
                        val dialogC = alertDialogC.create()
                        dialogC.show()
                    }
                }

                val dialog = alertDialog.create()

                dialog.show()
            }
            mybooksRV.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.library_menu, menu)
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