package com.openclassrooms.belivre.controllers.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.signature.ObjectKey
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.City
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.utils.GlideApp
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.CityViewModel
import com.openclassrooms.belivre.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.add_media_dialog.view.*
import java.io.File
import java.util.*


class ProfileActivity : AppCompatActivity(), LifecycleOwner {

    //////////////////////////////////////////////
    // ---------- MODELS / DATA --------------- //
    //////////////////////////////////////////////

    private var user: User? = null
    private var names: List<String>? = null
    private var city: City? = null

    //////////////////////////////////////////////
    // ----------- VIEW MODELS ---------------- //
    //////////////////////////////////////////////

    private val userVM: UserViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserViewModel() }).get(UserViewModel::class.java)
    }
    private val cityVM: CityViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { CityViewModel() }).get(CityViewModel::class.java)
    }

    private var filePath: Uri? = null

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var ref: StorageReference

    private lateinit var addMediaIV:ImageView
    private lateinit var filePathURI : Uri

    //////////////////////////////////////////////
    // ---------- FIREBASE USER --------------- //
    //////////////////////////////////////////////

    private var currentUser : FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = getString(R.string.profile)

        // Retrieving FirebaseAuth instance and current user
        val mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth?.currentUser

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        // Setup Places Client
        val apiKey = getString(R.string.belivre_google_map_key)
        if (apiKey.isNullOrEmpty()) Toast.makeText(this, getString(R.string.no_api_key), Toast.LENGTH_LONG).show()
        else { if (!Places.isInitialized()) Places.initialize(applicationContext, apiKey) }

        // Retrieving user from Firestore and configuring Toast message observer
        userVM.getUser(currentUser!!.uid).observe(this, Observer { user:User? -> this.updateUI(user)})
        userVM.toastMessage.observe(this, Observer { res ->
            if (res != null) {
                val message = getString(res)
                toast(message)
            }
        })

        // FAB action setup
        validateProfileFormFAB.setOnClickListener { validateForm() }

        // City field action setup (Google Places API)
        cityET.setOnFocusChangeListener { _, hasFocus ->  if(hasFocus) startAutoCompleteActivity()}

        profilePic.setOnClickListener { createAddMediaDialog() }
    }

    //////////////////////////////////////////////
    // --------------- UI --------------------- //
    //////////////////////////////////////////////

    /**
     * Updates UI elements based on user information retrieved from FireStore
     *
     * @param userRetrived User retrieved from FireStore
     */
    private fun updateUI(userRetrived: User?){
        if(userRetrived == null){
            names = currentUser?.displayName?.split(" ")
            user = User(currentUser?.uid,names?.get(1), names?.get(0), currentUser?.email, "", currentUser?.photoUrl.toString())
        }
        else{
            user = userRetrived
            if(!user?.cityId.isNullOrEmpty())cityVM.getCity(user?.cityId!!).observe(this, Observer { city:City? -> cityET.setText(city!!.name); this.city = city
            })
            ref = storageReference.child("images/profilePictures/${this.user!!.id.toString()}")
        }

        lastNameEt.setText(user!!.lastname)
        firstNameEt.setText(user!!.firstname)
        emailEt.setText(user!!.email)

        displayName.text = getString(R.string.profile_display_name, user!!.firstname, user!!.lastname?.substring(0,1))

        if(user!!.profilePicURL.equals("images/profilePictures/${this.user!!.id.toString()}")){
            GlideApp.with(this)
                .load(ref)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .fitCenter()
                .circleCrop()
                .into(profilePic)
        }
        else if (!user!!.profilePicURL!!.isEmpty()){
            GlideApp.with(this)
                .load(user!!.profilePicURL)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .fitCenter()
                .circleCrop()
                .into(profilePic)
        }
        else {
            user!!.profilePicURL = ""
            GlideApp.with(this)
                .load(R.drawable.ic_avatar)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .fitCenter()
                .circleCrop()
                .into(profilePic)
        }
    }

    //////////////////////////////////////////////
    // -------- GOOGLE PLACES API ------------- //
    //////////////////////////////////////////////

    /**
     * Launches the autocompletion activity of Google Places API
     *
     */
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

    //////////////////////////////////////////////
    // --------- FORM VALIDATION -------------- //
    //////////////////////////////////////////////

    /**
     * Retrieves the values of the different fields of the activity and updates the current user
     * If one of the field is empty, an error message appears
     *
     */
    private fun validateForm(){
        if(cityET != null && !lastNameEt.text.toString().isEmpty() && !firstNameEt.text.toString().isEmpty()) {
            user?.lastname = lastNameEt.text?.toString()
            user?.firstname = firstNameEt.text?.toString()
            user?.cityId = city?.id
            cityVM.addCity(city!!)
            userVM.addUser(user!!)
        }
        else
            this.toast(getString(R.string.empty_field))
    }

    //////////////////////////////////////////////
    // ---------- PROFILE PICTURE ------------- //
    //////////////////////////////////////////////

    private fun chooseImageFromGallery() {
      intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
      startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun chooseImageFromCamera(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),2)
        }
        else {
            val packageManager = Objects.requireNonNull(this).packageManager
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                val mainDirectory = File(Environment.getExternalStorageDirectory().absolutePath, "DCIM")
                if (!mainDirectory.exists()) mainDirectory.mkdirs()
                val calendar = Calendar.getInstance()
                filePathURI = Uri.fromFile(File(mainDirectory, "IMG_" + calendar.timeInMillis))
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, filePathURI)
                val builder = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
                startActivityForResult(intent, 2)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if(requestCode == 2){
                if (!grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImageFromCamera()
                }
        }
    }

    private fun uploadImage() {

        progressBar.visibility = View.VISIBLE
        Toast.makeText(this, "Upload in progress..", Toast.LENGTH_SHORT).show()

        ref.putFile(this.filePath!!)
            .addOnSuccessListener {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(this, "Image successfully uploaded!", Toast.LENGTH_SHORT).show()
                user!!.profilePicURL = "images/profilePictures/${this.user!!.id.toString()}"
                GlideApp.with(this)
                    .load(filePath)
                    .signature(ObjectKey(System.currentTimeMillis().toString()))
                    .fitCenter()
                    .circleCrop()
                    .into(profilePic)

            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                    .totalByteCount
                progressBar.progress = progress.toInt()
            }
    }

    @SuppressLint("InflateParams")
    private fun createAddMediaDialog(){
        filePath = null

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.add_media_dialog, null)

        dialogView.add_media_gallery.setOnClickListener { chooseImageFromGallery() }
        dialogView.add_media_camera.setOnClickListener { chooseImageFromCamera() }

        addMediaIV = dialogView.add_media_iv

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Upload", null)
            .setNeutralButton("Cancel"){ _, _ ->  }
            .setNegativeButton("Use default", null)

        val dialog = alertDialog.create()
        dialog.setOnShowListener {
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.setOnClickListener {
                if(filePath != null) {
                    uploadImage()
                    dialog.dismiss()
                }
                else {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Please select a picture first")
                        .setPositiveButton("Ok"){ _, _ ->  }
                        .show()
                }
            }

            val defaultButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            defaultButton.setOnClickListener {
                if(!currentUser!!.photoUrl.toString().isEmpty()) {
                    user!!.profilePicURL = currentUser!!.photoUrl.toString()
                    GlideApp.with(this)
                        .load(user!!.profilePicURL)
                        .fitCenter()
                        .signature(ObjectKey(System.currentTimeMillis().toString()))
                        .circleCrop()
                        .into(profilePic)
                    dialog.dismiss()
                }
                else {
                    user!!.profilePicURL = ""
                    GlideApp.with(this)
                        .load(R.drawable.ic_avatar)
                        .fitCenter()
                        .signature(ObjectKey(System.currentTimeMillis().toString()))
                        .circleCrop()
                        .into(profilePic)
                }
            }
        }

        dialog.show()
    }

    //////////////////////////////////////////////
    // -------- ON ACTIVITY RESULT ------------ //
    //////////////////////////////////////////////

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
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            filePath = data.data
            GlideApp.with(this)
                .load(filePath)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .fitCenter()
                .circleCrop()
                .into(addMediaIV)
        }
        if(resultCode == RESULT_OK && requestCode == CAMERA_IMAGE_REQUEST){
            filePath = filePathURI
            GlideApp.with(this)
                .load(filePath)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .fitCenter()
                .circleCrop()
                .into(addMediaIV)
        }
    }


    companion object {
        const val TAG = "ProfileActivity"

        const val AUTOCOMPLETE_REQUEST_CODE = 1

        const val PICK_IMAGE_REQUEST = 71
        const val CAMERA_IMAGE_REQUEST = 2

        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }
}
