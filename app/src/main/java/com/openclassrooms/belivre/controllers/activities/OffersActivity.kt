package com.openclassrooms.belivre.controllers.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import kotlinx.android.synthetic.main.activity_offers.*

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
                toast(item.userDisplayName.toString())
            }
            offersRV.adapter = adapter
            adapter.notifyDataSetChanged()
        }
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
