package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.Message

class MessageRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    // Add message to firebase
    fun addMessage(message: Message): Task<Void> {
        val documentReference = firestoreDB.collection("messages").document(message.id.toString())
        return documentReference.set(message)
    }

    // Get messages from firebase
    fun getMessages(): CollectionReference {
        return firestoreDB.collection("messages")
    }

    // Get message from firebase
    fun getMessage(id: String): DocumentReference {
        return firestoreDB.collection("messages").document(id)
    }

    // Delete message from firebase
    fun deleteMessage(message: Message): Task<Void> {
        val documentReference =  firestoreDB.collection("messages")
            .document(message.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "MESSAGE_REPOSITORY"
    }
}