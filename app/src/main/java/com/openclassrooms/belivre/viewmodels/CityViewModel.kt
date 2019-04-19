package com.openclassrooms.belivre.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kiwimob.firestore.livedata.livedata
import com.openclassrooms.belivre.models.City

class CityViewModel : ViewModel() {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getCities(): LiveData<List<City>> {
        return db
            .collection("cities")
            .livedata(City::class.java)
    }

    fun getCitiesByName(name : String): LiveData<List<City>> {
        return db
            .collection("cities")
            .whereArrayContains("name",name)
            .livedata(City::class.java)
    }

    fun getCity(id : String): LiveData<City> {
        return db
            .collection("cities")
            .document(id)
            .livedata(City::class.java)
    }

    fun addCity(city : City) : Task<Void> {
        return db.collection("cities")
            .document(city.id!!)
            .set(city, SetOptions.merge())
    }

    companion object{
        const val TAG = "CityViewModel"
    }
}