package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.chat.UserChatChannel
import com.openclassrooms.belivre.models.User

class UserChatChannelRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    // Add userChatChannel to firebase
    fun addUserChatChannel(uid: String, userChatChannel: UserChatChannel): Task<Void> {
        val documentReference = firestoreDB.collection("users").document(uid).collection("engagedChatChannels").document(userChatChannel.userId.toString())
        return documentReference.set(userChatChannel)
    }

    // Get user chat channels from firebase
    fun getUserChatChannels(uid: String): CollectionReference {
        return firestoreDB.collection("users")
            .document(uid)
            .collection("engagedChatChannels")
    }

    // Get user chat channel from firebase
    fun getUserChatChannel(uid: String, id:String): DocumentReference {
        return firestoreDB.collection("users")
            .document(uid)
            .collection("engagedChatChannels")
            .document(id)
    }

    // Delete user chat channel from firebase
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