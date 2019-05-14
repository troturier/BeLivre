package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.Book

class BookRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    // Add book to firebase
    fun addBook(book: Book): Task<Void> {
        val documentReference = firestoreDB.collection("books").document(book.id.toString())
        return documentReference.set(book)
    }

    fun updateBookRating(book: Book): Task<Void>{
        val documentReference = firestoreDB.collection("books").document(book.id.toString())
        return documentReference.update(
            "rating", book.rating
        )
    }

    // Get books from firebase
    fun getBooks(): CollectionReference {
        return firestoreDB.collection("books")
    }

    // Get book from firebase
    fun getBook(id: String): DocumentReference {
        return firestoreDB.collection("books").document(id)
    }

    // Delete book from firebase
    fun deleteBook(book: Book): Task<Void> {
        val documentReference =  firestoreDB.collection("books")
            .document(book.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "BOOK_REPOSITORY"
    }
}