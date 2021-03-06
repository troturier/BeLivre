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

/**
 * Activity displayed when a user want to see available books in a particular city
 * @property linearLayoutManager LinearLayoutManager
 * @property adapter CityRecyclerViewAdapter
 * @property cityID String
 * @property user User
 * @property userBookVM UserBookViewModel
 */
class CityActivity : AppCompatActivity(), LifecycleOwner {

    // UI
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CityRecyclerViewAdapter

    // DATA
    private lateinit var cityID: String
    private lateinit var user: User

    // VIEW MODEL
    private val userBookVM: UserBookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserBookViewModel() }).get(UserBookViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)

        // RECYCLER VIEW
        linearLayoutManager = LinearLayoutManager(this)
        cityRV.layoutManager = linearLayoutManager

        // Retrieving current user's data from intent
        user = intent.getSerializableExtra("user") as User

        // TOOLBAR
        setSupportActionBar(toolbar_city)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // Retrieving city's data from intent
        cityID = intent.getStringExtra("cityID")
        val cityName = intent.getStringExtra("cityName")

        toolbar_city.title = cityName

        // Retrieving UserBooks from Firestore
        userBookVM.getUserBooksByCity(cityID).observe(this, Observer { userBooks: List<UserBook>? -> configureRecyclerView(userBooks) })
    }

    /**
     * Configuring the RecyclerView
     * @param userBooks List<UserBook>? - Retrieved UserBooks from Firestore
     */
    private fun configureRecyclerView(userBooks: List<UserBook>?){
        if(userBooks != null){
            val sortedList:MutableList<UserBook>? = mutableListOf()
            for(ub in userBooks) {
                // Removing UserBooks of the current user
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

    /**
     * Displays an Alert Dialog when the user click on an item from the RecyclerView
     * @param userBook UserBook - Selected UserBook
     */
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
                // Opening DetailActivity
                val intent = DetailActivity.newIntent(this)
                intent.putExtra("id", userBook.bookId)
                intent.putExtra("user", user)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.cancel)) { _, _ -> }

        val dialog = alertDialog.create()

        // Opening ChatActivity
        dialogView.messageButtonOffersDialog.setOnClickListener {
            val intent = ChatActivity.newIntent(this)
            intent.putExtra("user_id", userBook.userId)
            intent.putExtra("user_name", userBook.userDisplayName)
            intent.putExtra("user_pp", userBook.userPicUrl)
            intent.putExtra("current_user", user)
            startActivity(intent)
        }

        // Displaying a new Alert Dialog for request confirmation
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
