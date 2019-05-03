package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.User

class UserRepository {

    private var firestoreDB = FirebaseFirestore.getInstance()

    // Add user to firebase
    fun addUser(user: User): Task<Void> {
        val documentReference = firestoreDB.collection("users").document(user.id.toString())
        return documentReference.set(user)
    }

    // Get users from firebase
    fun getUsers(): CollectionReference {
        return firestoreDB.collection("users")
    }

    // Get user from firebase
    fun getUser(uid: String): DocumentReference {
        return firestoreDB.collection("users").document(uid)
    }

    // Delete user from firebase
    fun deleteUser(user: User): Task<Void> {
        val documentReference =  firestoreDB.collection("users")
            .document(user.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "USER_REPOSITORY"
    }

}