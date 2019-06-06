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
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.CityRecyclerViewAdapter
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import kotlinx.android.synthetic.main.activity_city.*
import kotlinx.android.synthetic.main.offers_dialog.view.*

class CityActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CityRecyclerViewAdapter
    private lateinit var cityID: String
    private lateinit var user: User

    private val userBookVM: UserBookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserBookViewModel() }).get(UserBookViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        linearLayoutManager = LinearLayoutManager(this)
        cityRV.layoutManager = linearLayoutManager

        user = intent.getSerializableExtra("user") as User

        setSupportActionBar(toolbar_city)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        cityID = intent.getStringExtra("cityID")
        val cityName = intent.getStringExtra("cityName")

        toolbar_city.title = cityName

        userBookVM.getUserBooksByCity(cityID).observe(this, Observer { userBooks: List<UserBook>? -> configureRecyclerView(userBooks) })
    }

    private fun configureRecyclerView(userBooks: List<UserBook>?){
        if(userBooks != null){
            val sortedList:MutableList<UserBook>? = mutableListOf()
            for(ub in userBooks) {
                if (ub.userId.toString() != user.id) {
                    sortedList!!.add(ub)
                }
            }
            adapter = CityRecyclerViewAdapter(sortedList!!){item: UserBook, _: Int ->
                showDialog(item)
            }
            cityRV.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("InflateParams")
    private fun showDialog(userBook:UserBook){
        val layoutInflater = this.layoutInflater
        val dialogView = layoutInflater.inflate(R.layout.offers_dialog, null)

        dialogView.offersDialogTV.text = getString(R.string.borrowed_dialog_dn_borrowed_tab, userBook.userDisplayName)

        dialogView.textView7.text = userBook.title

        loadProfilePictureIntoImageView(dialogView.offersDialogIV, this, userBook.userPicUrl, userBook.userId.toString())

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton(getString(R.string.view_details)) { dialog, _ ->
                val intent = DetailActivity.newIntent(this)
                intent.putExtra("id", userBook.bookId)
                intent.putExtra("user", user)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.cancel)) { _, _ -> }

        val dialog = alertDialog.create()

        dialogView.messageButtonOffersDialog.setOnClickListener {
            val intent = ChatActivity.newIntent(this)
            intent.putExtra("user_id", userBook.userId)
            intent.putExtra("user_name", userBook.userDisplayName)
            intent.putExtra("user_pp", userBook.userPicUrl)
            intent.putExtra("current_user", user)
            startActivity(intent)
        }

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
            return Intent(context, CityActivity::class.java)
        }
    }
}
