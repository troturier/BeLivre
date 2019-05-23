package com.openclassrooms.belivre.controllers.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.openclassrooms.belivre.R
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment  : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

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
    }

    private fun moveCameraToUser(){
        if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }
        else{
            mMap.isMyLocationEnabled = true
            mFusedLocationProviderClient.lastLocation.addOnSuccessListener(activity!!) { location ->
                val userPostion = CameraPosition.builder().target(LatLng(location.latitude, location.longitude)).zoom(12f).bearing(0f).build()
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
}
