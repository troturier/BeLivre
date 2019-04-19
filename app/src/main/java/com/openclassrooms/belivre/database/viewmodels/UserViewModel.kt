package com.openclassrooms.belivre.database.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
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

    fun addUser(user : User){
        db.collection("users")
            .document(user.id!!)
            .set(user, SetOptions.merge())
    }
}