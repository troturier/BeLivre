package com.openclassrooms.belivre.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.BookReview
import com.openclassrooms.belivre.repositories.BookReviewRepository
import com.openclassrooms.belivre.utils.SingleLiveEvent

class BookReviewViewModel: ViewModel() {
    private var bookreviewRepository = BookReviewRepository()
    private var bookreviews : MutableLiveData<List<BookReview>> = MutableLiveData()
    private var bookreview : MutableLiveData<BookReview> = MutableLiveData()

    private val toastMessage = SingleLiveEvent<Int>()

    // save bookreview to firebase
    fun addBookReview(bookreview: BookReview){
        bookreviewRepository.addBookReview(bookreview)
            .addOnFailureListener {
                toastMessage.value = R.string.update_fail
            }
            .addOnSuccessListener {
                toastMessage.value = R.string.bookreview_updated_success
            }
    }

    // get realtime updates from firebase regarding saved bookreviews
    fun getBookReviews(bookId: String): LiveData<List<BookReview>>{
        bookreviewRepository.getBookReviews(bookId).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                bookreviews.value = null
                return@EventListener
            }

            val bookreviewList : MutableList<BookReview> = mutableListOf()
            for (doc in value!!) {
                val bookreview = doc.toObject(BookReview::class.java)
                bookreviewList.add(bookreview)
            }
            bookreviews.value = bookreviewList
        })

        return bookreviews
    }

    // get realtime updates from firebase regarding bookreview
    fun getBookReview(bookId: String, id: String): LiveData<BookReview> {
        bookreviewRepository.getBookReview(bookId, id).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                bookreviews.value = null
                return@EventListener
            }
            bookreview.value = value!!.toObject(BookReview::class.java)
        })

        return bookreview
    }

    // delete a bookreview from firebase
    fun deleteBookReview(bookreview: BookReview){
        bookreviewRepository.deleteBookReview(bookreview).addOnFailureListener {
            Log.e(TAG,"Failed to delete BookReview")
        }
    }

    companion object {
        const val TAG = "BOOKREVIEW_VIEW_MODEL"
    }
}