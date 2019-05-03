package com.openclassrooms.belivre.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.BookUser
import com.openclassrooms.belivre.repositories.BookUserRepository
import com.openclassrooms.belivre.utils.SingleLiveEvent

class BookUserViewModel : ViewModel() {
    private var bookuserRepository = BookUserRepository()
    private var bookusers : MutableLiveData<List<BookUser>> = MutableLiveData()
    private var bookuser : MutableLiveData<BookUser> = MutableLiveData()

    private val toastMessage = SingleLiveEvent<Int>()

    // save bookuser to firebase
    fun addBookUser(bookuser: BookUser){
        bookuserRepository.addBookUser(bookuser)
            .addOnFailureListener {
                toastMessage.value = R.string.update_fail
            }
            .addOnSuccessListener {
                toastMessage.value = R.string.book_updated_success
            }
    }

    // get realtime updates from firebase regarding saved bookusers
    fun getBookUsers(bookId: String): LiveData<List<BookUser>> {
        bookuserRepository.getBookUsers(bookId).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                bookusers.value = null
                return@EventListener
            }

            val bookuserList : MutableList<BookUser> = mutableListOf()
            for (doc in value!!) {
                val bookuser = doc.toObject(BookUser::class.java)
                bookuserList.add(bookuser)
            }
            bookusers.value = bookuserList
        })

        return bookusers
    }

    // get realtime updates from firebase regarding bookuser
    fun getBookUser(bookId: String, id: String): LiveData<BookUser> {
        bookuserRepository.getBookUser(bookId, id).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                bookusers.value = null
                return@EventListener
            }
            bookuser.value = value!!.toObject(BookUser::class.java)
        })

        return bookuser
    }

    // delete a bookuser from firebase
    fun deleteBookUser(bookuser: BookUser){
        bookuserRepository.deleteBookUser(bookuser).addOnFailureListener {
            Log.e(TAG,"Failed to delete BookUser")
        }
    }

    companion object {
        const val TAG = "BOOK_USER_VIEW_MODEL"
    }
}