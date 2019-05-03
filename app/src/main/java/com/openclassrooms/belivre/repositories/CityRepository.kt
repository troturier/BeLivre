package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.City

class CityRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    // Add city to firebase
    fun addCity(city: City): Task<Void> {
        val documentReference = firestoreDB.collection("cities").document(city.id.toString())
        return documentReference.set(city)
    }

    // Get cities from firebase
    fun getCities(): CollectionReference {
        return firestoreDB.collection("cities")
    }

    // Get city from firebase
    fun getCity(id: String): DocumentReference {
        return firestoreDB.collection("cities").document(id)
    }

    // Delete city from firebase
    fun deleteCity(city: City): Task<Void> {
        val documentReference =  firestoreDB.collection("cities")
            .document(city.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "CITY_REPOSITORY"
    }
}