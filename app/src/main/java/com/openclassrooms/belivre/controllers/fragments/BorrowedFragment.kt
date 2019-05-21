package com.openclassrooms.belivre.controllers.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.BorrowedRecyclerViewAdapter
import com.openclassrooms.belivre.adapters.RequestRecyclerViewAdapter
import com.openclassrooms.belivre.controllers.activities.DetailActivity
import com.openclassrooms.belivre.controllers.activities.LibraryActivity
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import kotlinx.android.synthetic.main.borrowed_dialog_borrowed_tab.view.*
import kotlinx.android.synthetic.main.fragment_borrowed.*
import kotlinx.android.synthetic.main.request_dialog_borrowed_tab.view.*

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
                showBorrowedDialog(item)
            }
            borrowedRVBorrowedFragment.adapter = adapterBorrowed
            adapterBorrowed.notifyDataSetChanged()


            val sortedUserBooksRequest = userBooksRequest!!.sortedWith(compareBy { it.title })
            adapterRequest = RequestRecyclerViewAdapter(sortedUserBooksRequest) { item: UserBook, _: Int ->
                showRequestDialog(item)
            }
            requestRVBorrowedFragment.adapter = adapterRequest
            adapterRequest.notifyDataSetChanged()
        }
    }

    @SuppressLint("InflateParams")
    private fun showBorrowedDialog(item: UserBook){
        val layoutInflater = this.layoutInflater
        val dialogView = layoutInflater.inflate(R.layout.borrowed_dialog_borrowed_tab, null)

        dialogView.borrowedTVDialogBorrowedTab.text = getString(R.string.borrowed_dialog_dn_borrowed_tab, item.userDisplayName)

        dialogView.textView5.text = item.title

        loadProfilePictureIntoImageView(dialogView.borrowedDialogIVBorrowedTab, activity!!.application, item.userPicUrl, item.userId!!)

        val alertDialog = AlertDialog.Builder(this.activity!!)
            .setView(dialogView)
            .setPositiveButton(getString(R.string.view_details)){ dialog, _ ->
                val intent = DetailActivity.newIntent(activity!!.applicationContext)
                intent.putExtra("id", item.bookId)
                intent.putExtra("user", LibraryActivity.user)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.cancel)) { _, _ -> }

        val dialog = alertDialog.create()

        dialogView.returnRequestButtonBorrowedDialogBorrowedTab.setOnClickListener {
            val alertDialogC = AlertDialog.Builder(this.activity!!)
                .setTitle(getString(R.string.return_request_title))
                .setMessage(getString(R.string.return_request_message))
                .setPositiveButton(getString(R.string.yes)) { dialogCS, _ ->
                    item.status = 4
                    userBookVM.updateUserBook(item)
                    dialogCS.dismiss()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.no)) { dialogCNS, _ ->
                    dialogCNS.dismiss()
                }
            val dialogC = alertDialogC.create()
            dialogC.show()
        }

        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showRequestDialog(item: UserBook){
        val layoutInflater = this.layoutInflater
        val dialogView = layoutInflater.inflate(R.layout.request_dialog_borrowed_tab, null)

        dialogView.requestTVDialogBorrowedTab.text = getString(R.string.borrowed_dialog_dn_borrowed_tab, item.userDisplayName)

        if(item.status == 2) dialogView.textView6.text = getString(R.string.exchange_requesst)
        else dialogView.textView6.text = getString(R.string.return_request)

        loadProfilePictureIntoImageView(dialogView.requestDialogIVBorrowedTab, activity!!.application, item.userPicUrl, item.userId!!)

        val alertDialog = AlertDialog.Builder(this.activity!!)
            .setView(dialogView)
            .setPositiveButton(getString(R.string.view_details)){ dialog, _ ->
                val intent = DetailActivity.newIntent(activity!!.applicationContext)
                intent.putExtra("id", item.bookId)
                intent.putExtra("user", LibraryActivity.user)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.cancel)) { _, _ -> }

        val dialog = alertDialog.create()

        dialogView.cancelRequestButtonDialogBorrowedTab.setOnClickListener {
            val alertDialogC = AlertDialog.Builder(this.activity!!)
                .setTitle("Cancel your request")
                .setMessage("Are you sure you want to cancel your request?")
                .setPositiveButton(getString(R.string.yes)) { dialogCS, _ ->
                    if(item.status == 2) item.status = 1
                    else item.status = 3
                    userBookVM.updateUserBook(item)
                    dialogCS.dismiss()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.no)) { dialogCNS, _ ->
                    dialogCNS.dismiss()
                }
            val dialogC = alertDialogC.create()
            dialogC.show()
        }

        dialog.show()
    }
}