package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.BookReview

class BookReviewRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    // Add review to firebase
    fun addBookReview(bookReview: BookReview): Task<Void> {
        val documentReference = firestoreDB
            .collection("books")
            .document(bookReview.bookId.toString())
            .collection("reviews")
            .document(bookReview.id.toString())

        return documentReference.set(bookReview)
    }

    // Get reviews of a book from firebase
    fun getBookReviews(bookId: String): CollectionReference {
        return firestoreDB
            .collection("books")
            .document(bookId)
            .collection("reviews")
    }

    // Get review of a book from firebase
    fun getBookReview(bookId: String, id: String): DocumentReference {
        return firestoreDB
            .collection("books")
            .document(bookId)
            .collection("reviews")
            .document(id)
    }

    // Delete review from firebase
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