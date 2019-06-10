package com.openclassrooms.belivre.controllers.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

/**
 * Chat Activity used to display messages between 2 users from a Chat Channel
 * @property currentUser User? - User currently using the app
 * @property currentChannelId String - Current channel id selected by the user
 * @property otherUserProfilePic String? - Other user's profile picture URL
 * @property otherUserId String - Other user's id
 * @property otherUserDisplayName String - Other user's display name
 * @property shouldInitRecyclerView Boolean - Indicates if the RecyclerView should be initialized or not
 * @property messagesListenerRegistration ListenerRegistration
 * @property messagesSection Section
 * @property notificationStart Boolean - Indicates if the activity has been started from a notification or from another activity
 * @property userChatChannelVM UserChatChannelViewModel
 * @property userVM UserViewModel
 */
class ChatActivity : AppCompatActivity(), LifecycleOwner {

    private var currentUser: User? = null
    private lateinit var currentChannelId: String

    private var otherUserProfilePic: String? = null
    private lateinit var otherUserId: String
    private lateinit var otherUserDisplayName: String

    private var shouldInitRecyclerView = true
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private lateinit var messagesSection: Section

    private var notificationStart = false

    // VIEW MODELS
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

        // Displays the other user's display name
        supportActionBar!!.title = intent.getStringExtra("user_name")

        // Retrieving the other user's information
        otherUserId = intent.getStringExtra("user_id")
        if (intent.hasExtra("user_pp")) {
            otherUserProfilePic = intent.getStringExtra("user_pp")
        }
        otherUserDisplayName = intent.getStringExtra("user_name")

        // Retrieving the current user's information
        if (intent.hasExtra("current_user")) {
            currentUser = intent.getSerializableExtra("current_user") as User
            initActivity(currentUser)
        } else {
            userVM.getUser(intent.getStringExtra("current_user_id")).observe(this, androidx.lifecycle.Observer { initActivity(it)})
            // The activity has been started from a notification
            notificationStart = true
        }

    }

    private fun initActivity(user: User?){
        // If the activity has been started from a notification we need to retrieve the current user from Firestore
        if (currentUser == null && user != null){
            currentUser = user
        }

        // Updating current user's ChatChannel on Firestore : sets the last received message as read / seen
        userChatChannelVM.getUserChatChannel(currentUser?.id!!, otherUserId).observe(this, androidx.lifecycle.Observer { updateSeenBoolean(it) })

        // Try retrieves the ChatChannel from Firestore. If it cannot be retrieved it will be created
        FirestoreUtil.getOrCreateChatChannel(this, otherUserId, otherUserProfilePic, otherUserDisplayName, currentUser!!) { channelId ->
            // Retrieves the ChatChannel ID and add a new Message Listener
            currentChannelId = channelId
            messagesListenerRegistration = FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            // Action performed when the "Send" button is pressed
            imageView_send.setOnClickListener {
                // Retrieving / Creating the TextMessage object to be sent
                val messageToSend =
                    TextMessage(
                        editText_message.text.toString(),
                        Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        otherUserId,
                        getString(
                            R.string.profile_display_name,
                            currentUser!!.firstname,
                            currentUser!!.lastname?.substring(0,1)
                        )
                    )

                // Updating Chat Channels of both users
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
                    getString(R.string.profile_display_name, currentUser!!.firstname, currentUser!!.lastname?.substring(0,1)),
                    editText_message.text.toString(),
                    false,
                    Calendar.getInstance().time
                )

                userChatChannelVM.addUserChatChannel(currentUser!!.id!!, currentUserCC)
                userChatChannelVM.addUserChatChannel(otherUserId, otherUserCC)

                // Restoring EditText value to default and sending the message to Firestore
                editText_message.setText("")
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }
        }
    }

    /**
     * Set a UserChannel's last message "Seen?" boolean to true
     * @param userChatChannel UserChatChannel?
     */
    private fun updateSeenBoolean(userChatChannel: UserChatChannel?){
        if (userChatChannel != null) {
            userChatChannel.seen = true
            userChatChannelVM.addUserChatChannel(currentUser!!.id!!, userChatChannel)
        }
    }

    /**
     * Update the RecyclerView with retrieved data
     * @param messages List<Item> - A list of message
     */
    private fun updateRecyclerView(messages: List<Item>) {
        // RecyclerView initialization
        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            // Since the RV has been initialized we can set shouldInitRecyclerView to false
            shouldInitRecyclerView = false
        }

        // Update RV's items
        fun updateItems() = messagesSection.update(messages)

        // Will update or initialize the RecyclerView according to shouldInitRecyclerView state
        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

        // Scroll to the latest message received
        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter!!.itemCount - 1)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Checks from where the Activity has been started and pass the current user as a Serializable Intent accordingly
        if(notificationStart){
            val intent = MainActivity.newIntent(this)
            intent.putExtra("currentUser", currentUser)
            startActivity(intent)
        }
        else onBackPressed()
        return true
    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, ChatActivity::class.java)
        }
    }
}
