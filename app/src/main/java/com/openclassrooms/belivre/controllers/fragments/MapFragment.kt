package com.openclassrooms.belivre.controllers.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.ui.IconGenerator
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.controllers.activities.CityActivity
import com.openclassrooms.belivre.controllers.activities.MainActivity
import com.openclassrooms.belivre.models.City
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.CityViewModel
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import kotlinx.android.synthetic.main.fragment_map.*

/**
 * Map Fragment used in MainActivity
 * @property mMap GoogleMap
 * @property mFusedLocationProviderClient FusedLocationProviderClient
 * @property userBookVM UserBookViewModel
 * @property cityVM CityViewModel
 */
class MapFragment  : Fragment(), OnMapReadyCallback, LifecycleOwner {

    // MAP
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    // VIEW MODELS
    private val userBookVM: UserBookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserBookViewModel() }).get(UserBookViewModel::class.java)
    }

    private val cityVM: CityViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { CityViewModel() }).get(CityViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        if(mapView != null){
            mapView.onCreate(null)
            mapView.onResume()
            mapView.getMapAsync(this)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        MapsInitializer.initialize(activity!!)
        mMap = p0
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity!!, R.raw.map_style))
        moveCameraToUser()

        cityVM.getCities().observe(this, Observer { cities:List<City>? -> setMarkers(cities)})
    }

    /**
     * Will move the map's camera to last known position of the user
     */
    private fun moveCameraToUser(){
        if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }
        else{
            mMap.isMyLocationEnabled = true
            mFusedLocationProviderClient.lastLocation.addOnSuccessListener(activity!!) { location ->
                val userPostion = CameraPosition.builder().target(LatLng(location.latitude, location.longitude)).zoom(10f).bearing(0f).build()
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(userPostion))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if(requestCode == 2){
            if (grantResults.isNotEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                moveCameraToUser()
            }
        }
    }

    /**
     * Creates markers (with counter) on the Map according to a list of cities
     * Only cities with at least one available book will be displayed
     * @param cities List<City>?
     */
    private fun setMarkers(cities: List<City>?){
        if(cities != null){
            for(city in cities){
                val markerOptions = MarkerOptions()
                markerOptions.position(LatLng(city.lat!!.toDouble(),city.long!!.toDouble()))
                markerOptions.title(city.name!!)
                markerOptions.snippet(city.id!!.toString())

                val iconFactory = IconGenerator(activity!!.applicationContext)
                iconFactory.setStyle(IconGenerator.STYLE_ORANGE)

                if (city.availabeUserBooks!! < 100) {
                    val iconBitmap = iconFactory.makeIcon(city.availabeUserBooks.toString())
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                }else{
                    val iconBitmap = iconFactory.makeIcon("99+")
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                }

                mMap.setOnMarkerClickListener { marker ->
                    userBookVM.getUserBooksByCityMap(marker.snippet)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userbookList: MutableList<UserBook> = mutableListOf()
                                for (document in task.result!!) {
                                    val userbook = document.toObject(UserBook::class.java)
                                    userbookList.add(userbook)
                                }
                                startCityActivity(userbookList, marker)
                            } else {
                                Log.d(UserBookViewModel.TAG, "Error getting documents: ", task.exception)
                            }
                        }
                    true
                }

                if(city.availabeUserBooks != 0){
                    mMap.addMarker(markerOptions)

                }

            }
        }
    }

    /**
     * Start the CityActivity
     * The activity will only start if the number of books available, other than those of the current user, is greater than one
     * @param userbooks List<UserBook>?
     * @param marker Marker
     */
    private fun startCityActivity(userbooks: List<UserBook>?, marker:Marker){
        if(userbooks != null && userbooks.isNotEmpty()){
            val sortedList:MutableList<UserBook>? = mutableListOf()
            for(ub in userbooks) {
                if (ub.userId.toString() != MainActivity.user.id.toString()) {
                    sortedList!!.add(ub)
                }
            }
            if (sortedList != null && sortedList.isNotEmpty()) {
                val intent = CityActivity.newIntent(activity!!.applicationContext)
                intent.putExtra("cityID", marker.snippet)
                intent.putExtra("cityName", marker.title)
                intent.putExtra("user", MainActivity.user)
                startActivity(intent)
            } else {
                activity!!.toast("Sorry, the only available books in this city are yours !")
            }
        }
    }
}
