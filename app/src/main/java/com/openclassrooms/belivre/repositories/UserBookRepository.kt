package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.UserBook

/**
 * UserBooks objects Repository for Firestore
 */
class UserBookRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    /**
     * Adds/Updates a UserBook to Firestore
     */
    fun addUserBook(userBook: UserBook): Task<Void> {
        val documentReference = firestoreDB
            .collection("userbooks")
            .document(userBook.id.toString())

        return documentReference.set(userBook)
    }

    /**
     * Retrieves all UserBooks from Firestore
     */
    fun getUserBooks(): CollectionReference {
        return firestoreDB
            .collection("userbooks")
    }

    /**
     * Retrieves one UserBook from Firestore
     */
    fun getUserBook(id: String): DocumentReference {
        return firestoreDB
            .collection("userbooks")
            .document(id)
    }

    /**
     * Deletes one UserBook from Firestore
     */
    fun deleteUserBook(userBook: UserBook): Task<Void> {
        val documentReference =  firestoreDB
            .collection("userbooks")
            .document(userBook.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "USER_BOOK_REPOSITORY"
    }
}