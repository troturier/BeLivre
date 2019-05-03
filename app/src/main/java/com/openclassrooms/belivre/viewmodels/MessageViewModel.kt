package com.openclassrooms.belivre.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.Message
import com.openclassrooms.belivre.repositories.MessageRepository
import com.openclassrooms.belivre.utils.SingleLiveEvent

class MessageViewModel : ViewModel(){
    private var messageRepository = MessageRepository()
    private var messages : MutableLiveData<List<Message>> = MutableLiveData()
    private var message : MutableLiveData<Message> = MutableLiveData()

    private val toastMessage = SingleLiveEvent<Int>()

    // save message to firebase
    fun addMessage(message: Message){
        messageRepository.addMessage(message)
            .addOnFailureListener {
                toastMessage.value = R.string.update_fail
            }
            .addOnSuccessListener {
                toastMessage.value = R.string.message_updated_success
            }
    }

    // get realtime updates from firebase regarding saved messages
    fun getMessages(): LiveData<List<Message>> {
        messageRepository.getMessages().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                messages.value = null
                return@EventListener
            }

            val messageList : MutableList<Message> = mutableListOf()
            for (doc in value!!) {
                val message = doc.toObject(Message::class.java)
                messageList.add(message)
            }
            messages.value = messageList
        })

        return messages
    }

    // get realtime updates from firebase regarding message
    fun getMessage(id: String): LiveData<Message> {
        messageRepository.getMessage(id).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                messages.value = null
                return@EventListener
            }
            message.value = value!!.toObject(Message::class.java)
        })

        return message
    }

    // delete a message from firebase
    fun deleteMessage(message: Message){
        messageRepository.deleteMessage(message).addOnFailureListener {
            Log.e(TAG,"Failed to delete Message")
        }
    }

    companion object {
        const val TAG = "MESSAGE_VIEW_MODEL"
    }
}