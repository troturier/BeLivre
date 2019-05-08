package com.openclassrooms.belivre.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.repositories.UserBookRepository
import com.openclassrooms.belivre.utils.SingleLiveEvent

class UserBookViewModel : ViewModel() {
    private var userbookRepository = UserBookRepository()
    private var userbooks : MutableLiveData<List<UserBook>> = MutableLiveData()
    private var userbook : MutableLiveData<UserBook> = MutableLiveData()

    private val toastMessage = SingleLiveEvent<Int>()

    // save userbook to firebase
    fun addUserBook(userbook: UserBook){
        userbookRepository.addUserBook(userbook)
            .addOnFailureListener {
                toastMessage.value = R.string.update_fail
            }
            .addOnSuccessListener {
                toastMessage.value = R.string.book_updated_success
            }
    }

    // get realtime updates from firebase regarding saved userbooks
    fun getUserBooks(userId: String): LiveData<List<UserBook>> {
        userbookRepository.getUserBooks(userId).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                userbooks.value = null
                return@EventListener
            }

            val userbookList : MutableList<UserBook> = mutableListOf()
            for (doc in value!!) {
                val userbook = doc.toObject(UserBook::class.java)
                userbookList.add(userbook)
            }
            userbooks.value = userbookList
        })

        return userbooks
    }

    // get realtime updates from firebase regarding userbook
    fun getUserBook(userId: String, id: String): LiveData<UserBook> {
        userbookRepository.getUserBook(userId, id).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                userbooks.value = null
                return@EventListener
            }
            userbook.value = value!!.toObject(UserBook::class.java)
        })

        return userbook
    }

    // delete a userbook from firebase
    fun deleteUserBook(userbook: UserBook){
        userbookRepository.deleteUserBook(userbook).addOnFailureListener {
            Log.e(TAG,"Failed to delete UserBook")
        }
    }

    companion object {
        const val TAG = "USER_BOOK_VIEW_MODEL"
    }
}