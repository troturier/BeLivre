package com.openclassrooms.belivre.controllers.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.CityRecyclerViewAdapter
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import kotlinx.android.synthetic.main.activity_city.*

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
                toast(item.title.toString())
            }
            cityRV.adapter = adapter
            adapter.notifyDataSetChanged()
        }
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
