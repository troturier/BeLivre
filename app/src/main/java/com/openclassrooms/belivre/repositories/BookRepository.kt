package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.Book

/**
 * Books objects Repository for Firestore
 */
class BookRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    /**
     * Adds/Updates a book object to Firestore
     */
    fun addBook(book: Book): Task<Void> {
        val documentReference = firestoreDB.collection("books").document(book.id.toString())
        return documentReference.set(book)
    }

    /**
     * Updates a book rating in Firestore
     */
    fun updateBookRating(book: Book): Task<Void>{
        val documentReference = firestoreDB.collection("books").document(book.id.toString())
        return documentReference.update(
            "rating", book.rating
        )
    }

    /**
     * Retrieves all books from Firestore
     */
    fun getBooks(): CollectionReference {
        return firestoreDB.collection("books")
    }

    /**
     * Retrieves one book from Firestore
     */
    fun getBook(id: String): DocumentReference {
        return firestoreDB.collection("books").document(id)
    }

    /**
     * Deletes one book from Firestore
     */
    fun deleteBook(book: Book): Task<Void> {
        val documentReference =  firestoreDB.collection("books")
            .document(book.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "BOOK_REPOSITORY"
    }
}