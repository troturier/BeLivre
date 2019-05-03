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

class UserViewModel : ViewModel(){

    val TAG = "USER_VIEW_MODEL"
    var userRepository = UserRepository()
    var users : MutableLiveData<List<User>> = MutableLiveData()
    var user : MutableLiveData<User> = MutableLiveData()

    internal val toastMessage = SingleLiveEvent<Int>()

    // save user to firebase
    fun addUser(user: User){
        userRepository.addUser(user)
        .addOnFailureListener {
            toastMessage.value = R.string.user_updated_fail
        }
        .addOnSuccessListener {
            toastMessage.value = R.string.user_updated_success
        }
    }

    // get realtime updates from firebase regarding saved users
    fun getUsers(): LiveData<List<User>>{
        userRepository.getUsers().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                users.value = null
                return@EventListener
            }

            var userList : MutableList<User> = mutableListOf()
            for (doc in value!!) {
                var user = doc.toObject(User::class.java)
                userList.add(user)
            }
            users.value = userList
        })

        return users
    }

    // get realtime updates from firebase regarding user
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

    // delete an user from firebase
    fun deleteUser(user: User){
        userRepository.deleteUser(user).addOnFailureListener {
            Log.e(TAG,"Failed to delete User")
        }
    }

}