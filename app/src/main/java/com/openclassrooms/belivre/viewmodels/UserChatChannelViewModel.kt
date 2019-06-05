package com.openclassrooms.belivre.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.chat.UserChatChannel
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.repositories.UserChatChannelRepository
import com.openclassrooms.belivre.utils.SingleLiveEvent

class UserChatChannelViewModel: ViewModel(){

    private var userChatChannelRepository = UserChatChannelRepository()
    private var userChatChannels : MutableLiveData<List<UserChatChannel>> = MutableLiveData()
    private var userChatChannel : MutableLiveData<UserChatChannel> = MutableLiveData()

    internal val toastMessage = SingleLiveEvent<Int>()

    // save userChatChannel to firebase
    fun addUserChatChannel(uid: String, userChatChannel: UserChatChannel){
        userChatChannelRepository.addUserChatChannel(uid, userChatChannel)
            .addOnFailureListener {
                toastMessage.value = R.string.update_fail
            }
            .addOnSuccessListener {
                toastMessage.value = R.string.user_updated_success
            }
    }

    // get realtime updates from firebase regarding saved userChatChannels
    fun getUserChatChannels(uid:String): LiveData<List<UserChatChannel>> {
        userChatChannelRepository.getUserChatChannels(uid).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                userChatChannels.value = null
                return@EventListener
            }

            val userChatChannelList : MutableList<UserChatChannel> = mutableListOf()
            for (doc in value!!) {
                val userChatChannel = doc.toObject(UserChatChannel::class.java)
                userChatChannelList.add(userChatChannel)
            }
            userChatChannels.value = userChatChannelList
        })

        return userChatChannels
    }

    // get realtime updates from firebase regarding userChatChannel
    fun getUserChatChannel(uid: String, id: String): LiveData<UserChatChannel>{
        userChatChannelRepository.getUserChatChannel(uid, id).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                userChatChannel.value = null
                return@EventListener
            }
            userChatChannel.value = value!!.toObject(UserChatChannel::class.java)
        })

        return userChatChannel
    }

    // delete an userChatChannel from firebase
    fun deleteUserChatChannel(user: User, id: String){
        userChatChannelRepository.deleteUserChatChannel(user, id).addOnFailureListener {
            Log.e(TAG,"Failed to delete UserChatChannel")
        }
    }

    companion object {
        const val TAG = "USERCC_VIEW_MODEL"
    }

}