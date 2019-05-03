package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.BookUser

class BookUserRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    // Add book to firebase
    fun addBookUser(bookUser: BookUser): Task<Void> {
        //var
        val documentReference = firestoreDB
            .collection("books")
            .document(bookUser.bookId.toString())
            .collection("users")
            .document(bookUser.id.toString())

        return documentReference.set(bookUser)
    }

    // Get users of a user from firebase
    fun getBookUsers(bookId: String): CollectionReference {
        return firestoreDB
            .collection("books")
            .document(bookId)
            .collection("users")
    }

    // Get book of a user from firebase
    fun getBookUser(bookId: String, id: String): DocumentReference {
        return firestoreDB
            .collection("books")
            .document(bookId)
            .collection("users")
            .document(id)
    }

    // Delete book from firebase
    fun deleteBookUser(bookUser: BookUser): Task<Void> {
        val documentReference =  firestoreDB
            .collection("books")
            .document(bookUser.bookId.toString())
            .collection("users")
            .document(bookUser.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "USER_BOOK_REPOSITORY"
    }
}