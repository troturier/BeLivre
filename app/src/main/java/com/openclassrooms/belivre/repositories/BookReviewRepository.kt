package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.BookReview

/**
 * BookReview objects Repository for Firestore
 */
class BookReviewRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    /**
     * Adds/Updates a new review to Firestore
     */
    fun addBookReview(bookReview: BookReview): Task<Void> {
        val documentReference = firestoreDB
            .collection("books")
            .document(bookReview.bookId.toString())
            .collection("reviews")
            .document(bookReview.id.toString())

        return documentReference.set(bookReview)
    }

    /**
     * Retrieves all reviews from Firestore
     */
    fun getBookReviews(bookId: String): CollectionReference {
        return firestoreDB
            .collection("books")
            .document(bookId)
            .collection("reviews")
    }

    /**
     * Retrieves one review from Firestore
     */
    fun getBookReview(bookId: String, id: String): DocumentReference {
        return firestoreDB
            .collection("books")
            .document(bookId)
            .collection("reviews")
            .document(id)
    }

    /**
     * Deletes one review from Firestore
     */
    fun deleteBookReview(bookReview: BookReview): Task<Void> {
        val documentReference =  firestoreDB
            .collection("books")
            .document(bookReview.bookId.toString())
            .collection("reviews")
            .document(bookReview.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "BOOK_REVIEW_REPOSITORY"
    }
}