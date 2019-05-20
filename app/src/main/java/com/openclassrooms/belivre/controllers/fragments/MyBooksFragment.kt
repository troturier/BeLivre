package com.openclassrooms.belivre.controllers.fragments

import android.annotation.SuppressLint
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
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import kotlinx.android.synthetic.main.borrowed_dialog.view.*
import kotlinx.android.synthetic.main.exchange_request_dialog.view.*
import kotlinx.android.synthetic.main.fragment_mybooks.*
import kotlinx.android.synthetic.main.return_request_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*


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
                when(item.status){
                    1 -> showAvailableDialog(item)
                    2 -> showRequestDialog(item)
                    3 -> showBorrowedDialog(item)
                    4 -> showReturnDialog(item)
                }
            }
            mybooksRV.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun showAvailableDialog(item: UserBook) {
        val alertDialog = AlertDialog.Builder(this.activity!!)
            .setTitle(item.title)
            .setMessage(getString(R.string.what_do_you_want))
            .setPositiveButton(getString(R.string.view_details)) { dialog, _ ->
                val intent = DetailActivity.newIntent(activity!!.applicationContext)
                intent.putExtra("id", item.bookId)
                intent.putExtra("user", LibraryActivity.user)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.cancel)) { _, _ -> }

        alertDialog.setNegativeButton(getString(R.string.delete)) { dialog, _ ->
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

        val dialog = alertDialog.create()

        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showRequestDialog(item : UserBook){
        val layoutInflater = this.layoutInflater
        val dialogView = layoutInflater.inflate(R.layout.exchange_request_dialog, null)

        dialogView.requestSenderTVDialog.text = getString(R.string.request_dialog_dn, item.requestSenderDisplayName)

        loadProfilePictureIntoImageView(dialogView.requestDialogIV, activity!!.application, item.requestSenderPicUrl, item.requestSenderId!!)

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

        dialogView.acceptButtonRequestDialog.setOnClickListener {
            val alertDialogC = AlertDialog.Builder(this.activity!!)
                .setTitle(getString(R.string.confirm_exchange))
                .setMessage(getString(R.string.are_you_sure_accept_exchange))
                .setPositiveButton(getString(R.string.yes)) { dialogCS, _ ->
                    item.lastBorrowerId = item.requestSenderId
                    item.lastBorrowerDisplayName = item.requestSenderDisplayName
                    item.lastBorrowerPicUrl = item.requestSenderPicUrl

                    val sdf = SimpleDateFormat(getString(R.string.date_pattern), Locale.getDefault())
                    val currentDate = sdf.format(Date())
                    item.lastBorrowedOn = currentDate

                    item.status = 3

                    item.requestSenderDisplayName = null
                    item.requestSenderId = null
                    item.requestSenderPicUrl = null

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

        dialogView.refuseExchangeButtonRequestDialog.setOnClickListener {
            val alertDialogC = AlertDialog.Builder(this.activity!!)
                .setTitle(getString(R.string.refuse_exchange))
                .setMessage(getString(R.string.are_you_sure_refuse_exchange))
                .setPositiveButton(getString(R.string.yes)) { dialogCS, _ ->
                    item.status = 1

                    item.requestSenderDisplayName = null
                    item.requestSenderId = null
                    item.requestSenderPicUrl = null

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
    private fun showReturnDialog(item: UserBook){
        val layoutInflater = this.layoutInflater
        val dialogView = layoutInflater.inflate(R.layout.return_request_dialog, null)

        dialogView.requestSenderTVReturnDialog.text = getString(R.string.return_dialog_dn, item.requestSenderDisplayName)

        loadProfilePictureIntoImageView(dialogView.requestReturnDialogIV, activity!!.application, item.requestSenderPicUrl, item.requestSenderId!!)

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

        dialogView.confirmButtonRequestDialog.setOnClickListener {
            val alertDialogC = AlertDialog.Builder(this.activity!!)
                .setTitle(getString(R.string.confirm_return))
                .setMessage(getString(R.string.are_you_sure_accept_return))
                .setPositiveButton(getString(R.string.yes)) { dialogCS, _ ->
                    item.status = 1

                    item.requestSenderDisplayName = null
                    item.requestSenderId = null
                    item.requestSenderPicUrl = null

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
    private fun showBorrowedDialog(item: UserBook){
        val layoutInflater = this.layoutInflater
        val dialogView = layoutInflater.inflate(R.layout.borrowed_dialog, null)

        dialogView.borrowedTVDialog.text = getString(R.string.borrowed_dialog_dn, item.lastBorrowerDisplayName)

        dialogView.textView4.text = item.title

        loadProfilePictureIntoImageView(dialogView.borrowedDialogIV, activity!!.application, item.lastBorrowerPicUrl, item.lastBorrowerId!!)

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
        dialog.show()
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