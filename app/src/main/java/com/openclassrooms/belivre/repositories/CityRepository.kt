package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.City

class CityRepository {
    val TAG = "CITY_REPOSITORY"
    var firestoreDB = FirebaseFirestore.getInstance()

    // Add city to firebase
    fun addCity(city: City): Task<Void> {
        //var
        var documentReference = firestoreDB.collection("cities").document(city!!.id.toString())
        return documentReference.set(city)
    }

    // Get cities from firebase
    fun getCities(): CollectionReference {
        var collectionReference = firestoreDB.collection("cities")
        return collectionReference
    }

    // Get city from firebase
    fun getCity(id: String): DocumentReference {
        var documentReference = firestoreDB.collection("cities").document(id)
        return documentReference
    }

    fun deleteCity(city: City): Task<Void> {
        var documentReference =  firestoreDB.collection("cities")
            .document(city!!.id.toString())

        return documentReference.delete()
    }
}