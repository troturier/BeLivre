package com.openclassrooms.belivre.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserWishlistBook
import com.openclassrooms.belivre.repositories.UserWishlistBookRepository
import com.openclassrooms.belivre.utils.SingleLiveEvent

class UserWishlistBookViewModel : ViewModel() {
    private var userwishlistbookRepository = UserWishlistBookRepository()
    private var userwishlistbooks : MutableLiveData<List<UserWishlistBook>> = MutableLiveData()
    private var userwishlistbook : MutableLiveData<UserWishlistBook> = MutableLiveData()

    private val toastMessage = SingleLiveEvent<Int>()

    // save userwishlistbook to firebase
    fun addUserWishlistBook(userwishlistbook: UserWishlistBook){
        userwishlistbookRepository.addUserWishlistBook(userwishlistbook)
            .addOnFailureListener {
                toastMessage.value = R.string.update_fail
            }
            .addOnSuccessListener {
                toastMessage.value = R.string.book_updated_success
            }
    }

    // get realtime updates from firebase regarding saved userwishlistbooks
    fun getUserWishlistBooks(): LiveData<List<UserWishlistBook>> {
        userwishlistbookRepository.getUserWishlistBooks().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                userwishlistbooks.value = null
                return@EventListener
            }

            val userwishlistbookList : MutableList<UserWishlistBook> = mutableListOf()
            for (doc in value!!) {
                val userwishlistbook = doc.toObject(UserWishlistBook::class.java)
                userwishlistbookList.add(userwishlistbook)
            }
            userwishlistbooks.value = userwishlistbookList
        })

        return userwishlistbooks
    }

    // get realtime updates from firebase regarding userwishlistbook
    fun getUserWishlistBook(id: String): LiveData<UserWishlistBook> {
        userwishlistbookRepository.getUserWishlistBook(id).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                userwishlistbooks.value = null
                return@EventListener
            }
            userwishlistbook.value = value!!.toObject(UserWishlistBook::class.java)
        })

        return userwishlistbook
    }

    // delete a userwishlistbook from firebase
    fun deleteUserWishlistBook(userwishlistbook: UserWishlistBook){
        userwishlistbookRepository.deleteUserWishlistBook(userwishlistbook).addOnFailureListener {
            Log.e(TAG,"Failed to delete UserWishlistBook")
        }
    }

    companion object {
        const val TAG = "USER_WISHLIST_BOOK_VM"
    }
}