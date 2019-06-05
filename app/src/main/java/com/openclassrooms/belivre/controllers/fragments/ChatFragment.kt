package com.openclassrooms.belivre.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.UserChatRecyclerViewAdapter
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment(), LifecycleOwner {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: UserChatRecyclerViewAdapter
    private var currentUser: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null

    private val userVM: UserViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserViewModel() }).get(UserViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth?.currentUser

        linearLayoutManager = LinearLayoutManager(activity)
        chatRV_main.layoutManager = linearLayoutManager

        userVM.getUsers().observe(this, Observer { users:List<User>? -> configureChatRecyclerView(users)})
    }

    private fun configureChatRecyclerView(users: List<User>?){
        if(users != null){
            val sortedList:MutableList<User>? = mutableListOf()
            for(u in users) {
                if (u.id.toString() != currentUser!!.uid) {
                    sortedList!!.add(u)
                }
            }
            adapter = UserChatRecyclerViewAdapter(sortedList!!){ item: User, _: Int ->
                // TODO
            }
            chatRV_main.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}
