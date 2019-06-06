package com.openclassrooms.belivre.controllers.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.chat.FirestoreUtil
import com.openclassrooms.belivre.chat.TextMessage
import com.openclassrooms.belivre.chat.UserChatChannel
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserChatChannelViewModel
import com.openclassrooms.belivre.viewmodels.UserViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

class ChatActivity : AppCompatActivity(), LifecycleOwner {
    private lateinit var currentChannelId: String
    private var currentUser: User? = null
    private lateinit var otherUserId: String
    private var otherUserProfilePic: String? = null
    private lateinit var otherUserDisplayName: String

    private lateinit var messagesListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    private val userChatChannelVM: UserChatChannelViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserChatChannelViewModel() }).get(UserChatChannelViewModel::class.java)
    }

    private val userVM: UserViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserViewModel() }).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setSupportActionBar(toolbar_chat)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        supportActionBar!!.title = intent.getStringExtra("user_name")

        otherUserId = intent.getStringExtra("user_id")
        if (intent.hasExtra("user_pp")) {
            otherUserProfilePic = intent.getStringExtra("user_pp")
        }
        otherUserDisplayName = intent.getStringExtra("user_name")

        if (intent.hasExtra("current_user")) {
            currentUser = intent.getSerializableExtra("current_user") as User
            initActivity(currentUser)
        } else {
            userVM.getUser(intent.getStringExtra("current_user_id")).observe(this, androidx.lifecycle.Observer { initActivity(it)})
        }

    }

    private fun initActivity(user: User?){
        if (currentUser == null && user != null){
            currentUser = user
            userChatChannelVM.getUserChatChannel(currentUser?.id!!, otherUserId).observe(this, androidx.lifecycle.Observer { updateSeenBoolean(it) })
            FirestoreUtil.getOrCreateChatChannel(this, otherUserId, otherUserProfilePic, otherUserDisplayName, currentUser!!) { channelId ->
                currentChannelId = channelId
                Log.d("testFCM", "HERE")
                messagesListenerRegistration =
                    FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

                imageView_send.setOnClickListener {
                    val messageToSend =
                        TextMessage(
                            editText_message.text.toString(),
                            Calendar.getInstance().time,
                            FirebaseAuth.getInstance().currentUser!!.uid,
                            otherUserId,
                            getString(
                                R.string.profile_display_name,
                                currentUser!!.firstname,
                                currentUser!!.lastname?.substring(0, 1)
                            )
                        )

                    val currentUserCC = UserChatChannel(
                        currentChannelId,
                        otherUserId,
                        otherUserProfilePic,
                        otherUserDisplayName,
                        editText_message.text.toString(),
                        true,
                        Calendar.getInstance().time
                    )
                    val otherUserCC = UserChatChannel(
                        currentChannelId,
                        currentUser!!.id,
                        currentUser!!.profilePicURL,
                        getString(
                            R.string.profile_display_name,
                            currentUser!!.firstname,
                            currentUser!!.lastname?.substring(0, 1)
                        ),
                        editText_message.text.toString(),
                        false,
                        Calendar.getInstance().time
                    )

                    userChatChannelVM.addUserChatChannel(currentUser!!.id!!, currentUserCC)
                    userChatChannelVM.addUserChatChannel(otherUserId, otherUserCC)

                    editText_message.setText("")
                    FirestoreUtil.sendMessage(messageToSend, channelId)
                }
            }
        }
        else{
            userChatChannelVM.getUserChatChannel(currentUser?.id!!, otherUserId).observe(this, androidx.lifecycle.Observer { updateSeenBoolean(it) })
            FirestoreUtil.getOrCreateChatChannel(this, otherUserId, otherUserProfilePic, otherUserDisplayName, currentUser!!) { channelId ->
                currentChannelId = channelId
                Log.d("testFCM", "HERE")
                messagesListenerRegistration = FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

                imageView_send.setOnClickListener {
                    val messageToSend =
                        TextMessage(editText_message.text.toString(), Calendar.getInstance().time,
                            FirebaseAuth.getInstance().currentUser!!.uid,
                            otherUserId, getString(R.string.profile_display_name, currentUser!!.firstname, currentUser!!.lastname?.substring(0,1)))

                    val currentUserCC = UserChatChannel(currentChannelId, otherUserId, otherUserProfilePic, otherUserDisplayName, editText_message.text.toString(), true, Calendar.getInstance().time)
                    val otherUserCC = UserChatChannel(currentChannelId, currentUser!!.id, currentUser!!.profilePicURL, getString(R.string.profile_display_name, currentUser!!.firstname, currentUser!!.lastname?.substring(0,1)), editText_message.text.toString(), false, Calendar.getInstance().time)

                    userChatChannelVM.addUserChatChannel(currentUser!!.id!!, currentUserCC)
                    userChatChannelVM.addUserChatChannel(otherUserId, otherUserCC)

                    editText_message.setText("")
                    FirestoreUtil.sendMessage(messageToSend, channelId)
                }
            }
        }
    }

    private fun updateSeenBoolean(userChatChannel: UserChatChannel?){
        if (userChatChannel != null) {
            userChatChannel.seen = true
            userChatChannelVM.addUserChatChannel(currentUser!!.id!!, userChatChannel)
        }
    }

    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter!!.itemCount - 1)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, ChatActivity::class.java)
        }
    }
}
