package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.UserBook

class UserBookRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    // Add book to firebase
    fun addUserBook(userBook: UserBook): Task<Void> {
        //var
        val documentReference = firestoreDB
            .collection("userbooks")
            .document(userBook.id.toString())

        return documentReference.set(userBook)
    }

    // Get books of a user from firebase
    fun getUserBooks(): CollectionReference {
        return firestoreDB
            .collection("userbooks")
    }

    // Get book of a user from firebase
    fun getUserBook(id: String): DocumentReference {
        return firestoreDB
            .collection("userbooks")
            .document(id)
    }

    // Delete book from firebase
    fun deleteUserBook(userBook: UserBook): Task<Void> {
        val documentReference =  firestoreDB
            .collection("users")
            .document(userBook.userId.toString())
            .collection("books")
            .document(userBook.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "USER_BOOK_REPOSITORY"
    }
}