package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.UserWishlistBook

class UserWishlistBookRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    // Add userwishlistbook to firebase
    fun addUserWishlistBook(userwishlistbook: UserWishlistBook): Task<Void> {
        val documentReference = firestoreDB.collection("userwishlistbooks").document(userwishlistbook.id.toString())
        return documentReference.set(userwishlistbook)
    }

    // Get userwishlistbooks from firebase
    fun getUserWishlistBooks(): CollectionReference {
        return firestoreDB.collection("userwishlistbooks")
    }

    // Get userwishlistbook from firebase
    fun getUserWishlistBook(uid: String): DocumentReference {
        return firestoreDB.collection("userwishlistbooks").document(uid)
    }

    // Delete userwishlistbook from firebase
    fun deleteUserWishlistBook(userwishlistbook: UserWishlistBook): Task<Void> {
        val documentReference =  firestoreDB.collection("userwishlistbooks")
            .document(userwishlistbook.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "USER_WISHLIST_BOOK_REPOSITORY"
    }
}