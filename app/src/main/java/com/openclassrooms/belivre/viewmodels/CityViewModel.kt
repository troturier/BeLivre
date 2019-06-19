package com.openclassrooms.belivre.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.belivre.models.City
import com.openclassrooms.belivre.repositories.CityRepository

/**
 * City ViewModel
 */
class CityViewModel : ViewModel() {
    private var cityRepository = CityRepository()
    private var cities : MutableLiveData<List<City>> = MutableLiveData()
    private var city : MutableLiveData<City> = MutableLiveData()

    /**
     * Saves City object in Firestore
     */
    fun addCity(city: City){
        cityRepository.getCity(city.id.toString()).get().addOnSuccessListener { value ->
            if(!value!!.exists()) {
                cityRepository.addCity(city)
                    .addOnFailureListener {
                        Log.e(TAG, "Failed to add City!")
                    }
            }
        }
    }

    /**
     * Retrieves all Cities from Firestore
     */
    fun getCities(): LiveData<List<City>>{
        cityRepository.getCities().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                cities.value = null
                return@EventListener
            }

            val cityList : MutableList<City> = mutableListOf()
            for (doc in value!!) {
                val city = doc.toObject(City::class.java)
                cityList.add(city)
            }
            cities.value = cityList
        })

        return cities
    }

    /**
     * Retrieves one City from Firestore
     */
    fun getCity(id: String): LiveData<City>{
        cityRepository.getCity(id).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                cities.value = null
                return@EventListener
            }
            city.value = value!!.toObject(City::class.java)
        })

        return city
    }

    /**
     * Deletes one City from Firestore
     */
    fun deleteCity(city: City){
        cityRepository.deleteCity(city).addOnFailureListener {
            Log.e(TAG,"Failed to delete City")
        }
    }

    companion object {
        const val TAG = "CITY_VIEW_MODEL"
    }
}