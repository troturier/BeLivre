package com.openclassrooms.belivre.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.models.City

/**
 * Cities objects Repository for Firestore
 */
class CityRepository {
    private var firestoreDB = FirebaseFirestore.getInstance()

    /**
     * Adds/Updates a city to Firestore
     */
    fun addCity(city: City): Task<Void> {
        val documentReference = firestoreDB.collection("cities").document(city.id.toString())
        return documentReference.set(city)
    }

    /**
     * Retrieves all cities from Firestore
     */
    fun getCities(): CollectionReference {
        return firestoreDB.collection("cities")
    }

    /**
     * Retrieves one city from Firestore
     */
    fun getCity(id: String): DocumentReference {
        return firestoreDB.collection("cities").document(id)
    }

    /**
     * Deletes one city from Firestore
     */
    fun deleteCity(city: City): Task<Void> {
        val documentReference =  firestoreDB.collection("cities")
            .document(city.id.toString())

        return documentReference.delete()
    }

    companion object {
        const val TAG = "CITY_REPOSITORY"
    }
}