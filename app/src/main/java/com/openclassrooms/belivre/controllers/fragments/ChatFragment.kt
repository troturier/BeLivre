package com.openclassrooms.belivre.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.UserChatRecyclerViewAdapter
import com.openclassrooms.belivre.chat.UserChatChannel
import com.openclassrooms.belivre.controllers.activities.ChatActivity
import com.openclassrooms.belivre.controllers.activities.MainActivity
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserChatChannelViewModel
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment(), LifecycleOwner {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: UserChatRecyclerViewAdapter
    private var currentUser: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null

    private val userChatChannelVM: UserChatChannelViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserChatChannelViewModel() }).get(UserChatChannelViewModel::class.java)
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

        if (currentUser != null) {
            userChatChannelVM.getUserChatChannels(currentUser!!.uid).observe(this, Observer { configureChatRecyclerView(it) })
        }
    }

    private fun configureChatRecyclerView(userChatChannels: List<UserChatChannel>?){
        if(userChatChannels != null && currentUser != null){
            val sortedList = userChatChannels.sortedWith(compareBy { it.time })
            val sortedList2: MutableList<UserChatChannel> = mutableListOf()

            for (ucc in sortedList){
                if (!ucc.lastMessage.isNullOrEmpty()){
                    sortedList2.add(ucc)
                }
            }

            adapter = UserChatRecyclerViewAdapter(sortedList2){ item: UserChatChannel, _: Int ->
                val intent = ChatActivity.newIntent(activity!!.applicationContext)
                intent.putExtra("user_id", item.userId)
                intent.putExtra("user_name", item.displayName)
                intent.putExtra("user_pp", item.profilePic)
                intent.putExtra("current_user", MainActivity.user)
                startActivity(intent)
            }
            chatRV_main.adapter = adapter
            adapter.notifyDataSetChanged()

            if(sortedList2.isEmpty()){
                chatRV_main.visibility = View.GONE
                phChatTab.visibility = View.VISIBLE
            }else{
                chatRV_main.visibility = View.VISIBLE
                phChatTab.visibility = View.GONE
            }

            var count = 0
            for(ucc in sortedList2){
                if (!ucc.seen){
                    count++
                }
            }

            if (count > 0) {
                activity!!.findViewById<AHBottomNavigation>(R.id.bottom_navigation_main).setNotification(count.toString(),2)
                activity!!.findViewById<AHBottomNavigation>(R.id.bottom_navigation_main).setNotificationBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorAccent))
            }else{
                activity!!.findViewById<AHBottomNavigation>(R.id.bottom_navigation_main).setNotification("", 2)
            }
        }
    }
}
