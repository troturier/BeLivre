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
import com.openclassrooms.belivre.utils.SingleLiveEvent

class CityViewModel : ViewModel() {
    val TAG = "CITY_VIEW_MODEL"
    var cityRepository = CityRepository()
    var cities : MutableLiveData<List<City>> = MutableLiveData()
    var city : MutableLiveData<City> = MutableLiveData()

    // save city to firebase
    fun addCity(city: City){
        cityRepository.addCity(city)
            .addOnFailureListener {
                Log.e(TAG,"Failed to add City!")
            }
    }

    // get realtime updates from firebase regarding saved cities
    fun getCities(): LiveData<List<City>>{
        cityRepository.getCities().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                cities.value = null
                return@EventListener
            }

            var cityList : MutableList<City> = mutableListOf()
            for (doc in value!!) {
                var city = doc.toObject(City::class.java)
                cityList.add(city)
            }
            cities.value = cityList
        })

        return cities
    }

    // get realtime updates from firebase regarding city
    fun getCity(uid: String): LiveData<City>{
        cityRepository.getCity(uid).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                cities.value = null
                return@EventListener
            }
            city.value = value!!.toObject(City::class.java)
        })

        return city
    }

    // delete an city from firebase
    fun deleteCity(city: City){
        cityRepository.deleteCity(city).addOnFailureListener {
            Log.e(TAG,"Failed to delete City")
        }
    }
}