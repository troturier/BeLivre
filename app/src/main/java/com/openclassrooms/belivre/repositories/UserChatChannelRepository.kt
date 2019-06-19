package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.chat.UserChatChannel
import com.openclassrooms.belivre.models.User

/**
 * UserChatChannels objects Repository for Firestore
 */
class UserChatChannelRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    /**
     * Adds/Updates a UserChatChannel to Firestore
     */
    fun addUserChatChannel(uid: String, userChatChannel: UserChatChannel): Task<Void> {
        val documentReference = firestoreDB.collection("users").document(uid).collection("engagedChatChannels").document(userChatChannel.userId.toString())
        return documentReference.set(userChatChannel)
    }

    /**
     * Retrieves all UserChatChannels from Firestore
     */
    fun getUserChatChannels(uid: String): CollectionReference {
        return firestoreDB.collection("users")
            .document(uid)
            .collection("engagedChatChannels")
    }

    /**
     * Retrieves one UserChatChannel from Firestore
     */
    fun getUserChatChannel(uid: String, id:String): DocumentReference {
        return firestoreDB.collection("users")
            .document(uid)
            .collection("engagedChatChannels")
            .document(id)
    }

    /**
     * Deletes one UserChatChannel from Firestore
     */
    fun deleteUserChatChannel(user: User, id:String): Task<Void> {
        val documentReference =  firestoreDB.collection("users")
            .document(user.id.toString())
            .collection("engagedChatChannels")
            .document(id)

        return documentReference.delete()
    }

    companion object {
        const val TAG = "USERCC_REPOSITORY"
    }
}