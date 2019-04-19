package com.openclassrooms.belivre.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kiwimob.firestore.livedata.livedata
import com.openclassrooms.belivre.models.User

class UserViewModel : ViewModel() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getUsers(): LiveData<List<User>>{
        return db
            .collection("users")
            .livedata(User::class.java)
    }

    fun getUser(uid : String): LiveData<User>{
        return db
            .collection("users")
            .document(uid)
            .livedata(User::class.java)
    }

    fun addUser(user : User) : Task<Void> {
        return db.collection("users")
            .document(user.id!!)
            .set(user, SetOptions.merge())
    }

    companion object{
        const val TAG = "UserViewModel"
    }
}