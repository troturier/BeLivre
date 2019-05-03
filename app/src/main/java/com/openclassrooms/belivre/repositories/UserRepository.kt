package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.User

class UserRepository {

    val TAG = "USER_REPOSITORY"
    var firestoreDB = FirebaseFirestore.getInstance()

    // Add user to firebase
    fun addUser(user: User): Task<Void> {
        //var
        var documentReference = firestoreDB.collection("users").document(user!!.id.toString())
        return documentReference.set(user)
    }

    // Get users from firebase
    fun getUsers(): CollectionReference {
        var collectionReference = firestoreDB.collection("users")
        return collectionReference
    }

    // Get user from firebase
    fun getUser(uid: String): DocumentReference {
        var documentReference = firestoreDB.collection("users").document(uid)
        return documentReference
    }

    fun deleteUser(user: User): Task<Void> {
        var documentReference =  firestoreDB.collection("users")
            .document(user!!.id.toString())

        return documentReference.delete()
    }

}