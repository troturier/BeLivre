package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.User

/**
 * Users objects Repository for Firestore
 */
class UserRepository {

    private var firestoreDB = FirebaseFirestore.getInstance()

    /**
     * Adds/Updates a User to Firestore
     */
    fun addUser(user: User): Task<Void> {
        val documentReference = firestoreDB.collection("users").document(user.id.toString())
        return documentReference.set(user)
    }

    /**
     * Retrieves all Users from Firestore
     */
    fun getUsers(): CollectionReference {
        return firestoreDB.collection("users")
    }

    /**
     * Retrieves one User from Firestore
     */
    fun getUser(uid: String): DocumentReference {
        return firestoreDB.collection("users").document(uid)
    }

    /**
     * Deletes one User from Firestore
     */
    fun deleteUser(user: User): Task<Void> {
        val documentReference =  firestoreDB.collection("users")
            .document(user.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "USER_REPOSITORY"
    }

}