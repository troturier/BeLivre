package com.openclassrooms.belivre.controllers.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.OffersRecyclerViewAdapter
import com.openclassrooms.belivre.models.Book
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.offers_dialog.view.*

class OffersActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: OffersRecyclerViewAdapter
    private lateinit var user: User
    private lateinit var book: Book
    private var currentUser: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null

    private val userBookVM: UserBookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserBookViewModel() }).get(UserBookViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)

        user = intent.getSerializableExtra("user") as User
        book = intent.getSerializableExtra("book") as Book

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth?.currentUser

        linearLayoutManager = LinearLayoutManager(this)
        offersRV.layoutManager = linearLayoutManager

        userBookVM.getAvailableUserBooksByBookId(book.id.toString()).observe(this, Observer { userbooks: List<UserBook>? -> configureOffersRecyclerView(userbooks) })

        setSupportActionBar(toolbar_offers)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        toolbar_offers.title = book.title
    }

    private fun configureOffersRecyclerView(userbooks: List<UserBook>?){
        if(userbooks != null){
            val sortedList:MutableList<UserBook>? = mutableListOf()
            for(ub in userbooks) {
                if (ub.userId.toString() != currentUser!!.uid) {
                    sortedList!!.add(ub)
                }
            }
            adapter = OffersRecyclerViewAdapter(sortedList!!){item: UserBook, _: Int ->
                showOffersDialog(item)
            }
            offersRV.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("InflateParams")
    private fun showOffersDialog(userBook:UserBook){
        val layoutInflater = this.layoutInflater
        val dialogView = layoutInflater.inflate(R.layout.offers_dialog, null)

        dialogView.offersDialogTV.text = getString(R.string.borrowed_dialog_dn_borrowed_tab, userBook.userDisplayName)

        dialogView.textView7.text = userBook.title

        loadProfilePictureIntoImageView(dialogView.offersDialogIV, this, userBook.userPicUrl, userBook.userId.toString())

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setNeutralButton(getString(R.string.cancel)) { _, _ -> }

        val dialog = alertDialog.create()

        dialogView.requestButtonOffersDialog.setOnClickListener {
            val alertDialogC = AlertDialog.Builder(this)
                .setTitle(getString(R.string.send_a_borrowing_request))
                .setMessage(getString(R.string.are_you_sure_request_borrow))
                .setPositiveButton(getString(R.string.yes)) { dialogCS, _ ->
                    userBook.requestSenderDisplayName = getString(R.string.profile_display_name, user.firstname, user.lastname?.substring(0,1))
                    userBook.requestSenderId = user.id
                    userBook.requestSenderPicUrl = user.profilePicURL

                    userBook.status = 2

                    userBookVM.updateUserBook(userBook)

                    dialogCS.dismiss()
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton(getString(R.string.no)) { dialogCNS, _ ->
                    dialogCNS.dismiss()
                }
            val dialogC = alertDialogC.create()
            dialogC.show()
        }

        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, OffersActivity::class.java)
        }
    }
}
