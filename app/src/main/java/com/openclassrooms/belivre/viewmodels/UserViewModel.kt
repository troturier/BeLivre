package com.openclassrooms.belivre.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.repositories.UserRepository
import com.openclassrooms.belivre.utils.SingleLiveEvent

/**
 * User ViewModel
 */
class UserViewModel : ViewModel(){

    private var userRepository = UserRepository()
    private var users : MutableLiveData<List<User>> = MutableLiveData()
    private var user : MutableLiveData<User> = MutableLiveData()

    internal val toastMessage = SingleLiveEvent<Int>()

    /**
     * Saves User object in Firestore
     */
    fun addUser(user: User){
        userRepository.addUser(user)
        .addOnFailureListener {
            toastMessage.value = R.string.update_fail
        }
        .addOnSuccessListener {
            toastMessage.value = R.string.user_updated_success
        }
    }

    /**
     * Retrieves all Users from Firestore
     */
    fun getUsers(): LiveData<List<User>>{
        userRepository.getUsers().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                users.value = null
                return@EventListener
            }

            val userList : MutableList<User> = mutableListOf()
            for (doc in value!!) {
                val user = doc.toObject(User::class.java)
                userList.add(user)
            }
            users.value = userList
        })

        return users
    }

    /**
     * Retrieves one User from Firestore
     */
    fun getUser(uid: String): LiveData<User>{
        userRepository.getUser(uid).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                users.value = null
                return@EventListener
            }
            user.value = value!!.toObject(User::class.java)
        })

        return user
    }

    /**
     * Deletes one User from Firestore
     */
    fun deleteUser(user: User){
        userRepository.deleteUser(user).addOnFailureListener {
            Log.e(TAG,"Failed to delete User")
        }
    }

    companion object {
        const val TAG = "USER_VIEW_MODEL"
    }

}