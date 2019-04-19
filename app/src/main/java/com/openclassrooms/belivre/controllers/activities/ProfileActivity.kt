package com.openclassrooms.belivre.controllers.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.City
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.CityViewModel
import com.openclassrooms.belivre.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*


class ProfileActivity : AppCompatActivity(), LifecycleOwner {

    private var user: User? = null

    private var city: City? = null

    private val userVM: UserViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserViewModel() }).get(UserViewModel::class.java)
    }
    
    private val cityVM: CityViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { CityViewModel() }).get(CityViewModel::class.java)
    }

    private var currentUser : FirebaseUser? = null

    private var names: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = getString(R.string.profile)

        val mAuth = FirebaseAuth.getInstance()

        currentUser = mAuth?.currentUser

        val apiKey = getString(R.string.belivre_google_map_key)

        if (apiKey.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.no_api_key), Toast.LENGTH_LONG).show()
            return
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        userVM.getUser(currentUser!!.uid).observe(this, Observer { user:User? -> this.updateUI(user)})

        validateProfileFormFAB.setOnClickListener { validateForm() }

        cityET.setOnFocusChangeListener { _, hasFocus ->  if(hasFocus) startAutoCompleteActivity()}
    }

    private fun updateUI(userRetrived: User?){
        if(userRetrived == null){
            names = currentUser?.displayName?.split(" ")
            user = User(currentUser?.uid,names?.get(1), names?.get(0), currentUser?.email, "", currentUser?.photoUrl.toString())
        }
        else{
            user = userRetrived
            if(!user?.cityId.isNullOrEmpty())cityVM.getCity(user?.cityId!!).observe(this, Observer { city:City? -> updateCity(city!!) })
        }

        lastNameEt.setText(user!!.lastname)
        firstNameEt.setText(user!!.firstname)
        emailEt.setText(user!!.email)

        displayName.text = getString(R.string.profile_display_name, user!!.firstname, user!!.lastname?.substring(0,1))

        Glide.with(this)
            .load(user!!.profilePicURL)
            .fitCenter()
            .circleCrop()
            .into(profilePic)
    }

    private fun updateCity(city: City){
        cityET.setText(city.name)
    }

    private fun startAutoCompleteActivity(){
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        )
            .setTypeFilter(TypeFilter.CITIES)
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun validateForm(){
        if(cityET != null) {
            user?.lastname = lastNameEt.text?.toString()
            user?.firstname = firstNameEt.text?.toString()
            user?.cityId = city?.id
            cityVM.addCity(city!!)
            userVM.addUser(user!!)
                .addOnSuccessListener {
                    Log.i(UserViewModel.TAG, getString(R.string.update_success))
                    this.toast(getString(R.string.update_success))
                }
                .addOnFailureListener { exception ->
                    Log.w(UserViewModel.TAG, getString(R.string.update_error), exception)
                    this.toast(getString(R.string.update_error))
                }
        }
        else
            this.toast(getString(R.string.select_city_first))
    }

    private fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    city = City(place.id, place.name, place.latLng!!.latitude.toString(), place.latLng!!.longitude.toString())
                    cityET.setText(place.name)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i(TAG, status.statusMessage)
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            cityET.clearFocus()
        }
    }


    companion object {
        const val TAG = "ProfileActivity"

        const val AUTOCOMPLETE_REQUEST_CODE = 1

        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }
}
