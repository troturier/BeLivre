package com.openclassrooms.belivre.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.repositories.UserBookRepository
import com.openclassrooms.belivre.utils.SingleLiveEvent

/**
 * BookReview ViewModel
 */
class UserBookViewModel : ViewModel() {
    private var userbookRepository = UserBookRepository()
    private var userbooks : MutableLiveData<List<UserBook>> = MutableLiveData()
    private var userbook : MutableLiveData<UserBook> = MutableLiveData()
    private var exist : Boolean = false

    private val toastMessage = SingleLiveEvent<Int>()

    /**
     * Saves BookReview object in Firestore
     */
    fun addUserBook(userbook: UserBook){
        userbookRepository.getUserBook(userbook.id!!).get().addOnSuccessListener { value ->
            if (!value!!.exists()) {
                userbookRepository.addUserBook(userbook)
                    .addOnFailureListener {
                        toastMessage.value = R.string.update_fail
                    }
                    .addOnSuccessListener {
                        toastMessage.value = R.string.book_updated_success
                    }
            }
        }
    }

    /**
     * Retrieves all BookReviews from Firestore
     */
    fun getUserBooksByUserId(userId: String): LiveData<List<UserBook>> {
        userbookRepository.getUserBooks()
            .whereEqualTo("userId", userId)
            .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
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

    /**
     * Retrieves available UserBooks from Firestore
     */
    fun getAvailableUserBooksByBookId(bookId: String): LiveData<List<UserBook>> {
        userbookRepository.getUserBooks()
            .whereEqualTo("bookId", bookId)
            .whereEqualTo("status", 1)
            .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
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

    /**
     * Retrieves UserBooks from Firestore according to a RequestSender id
     */
    fun getRequestUserBooksByRequestSenderId(requestSenderId: String): LiveData<List<UserBook>> {
        userbookRepository.getUserBooks()
            .whereEqualTo("requestSenderId", requestSenderId)
            .whereGreaterThan("status", 1)
            .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
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

    /**
     * Retrieves UserBooks from Firestore according to a City id
     */
    fun getUserBooksByCity(cityId: String): LiveData<List<UserBook>> {
        userbookRepository.getUserBooks()
            .whereEqualTo("cityId", cityId)
            .whereEqualTo("status", 1)
            .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
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

    /**
     * Retrieves UserBooks from Firestore according to a City id
     */
    fun getUserBooksByCityMap(cityId: String): Task<QuerySnapshot> {
        return userbookRepository.getUserBooks()
            .whereEqualTo("cityId", cityId)
            .whereEqualTo("status", 1)
            .get()
    }

    /**
     * Retrieves one BookReview from Firestore
     */
    fun getUserBook(id: String): LiveData<UserBook> {
        userbookRepository.getUserBook(id).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                userbooks.value = null
                return@EventListener
            }
            userbook.value = value!!.toObject(UserBook::class.java)
        })

        return userbook
    }

    /**
     * Updates an UserBook in Firestore
     */
    fun updateUserBook(userbook: UserBook){
        userbookRepository.addUserBook(userbook).addOnFailureListener {
            Log.e(TAG,"Failed to update UserBook")
        }
    }

    /**
     * Deletes one BookReview from Firestore
     */
    fun deleteUserBook(userbook: UserBook){
        userbookRepository.deleteUserBook(userbook).addOnFailureListener {
            Log.e(TAG,"Failed to delete UserBook")
        }
    }

    companion object {
        const val TAG = "USER_BOOK_VIEW_MODEL"
    }
}