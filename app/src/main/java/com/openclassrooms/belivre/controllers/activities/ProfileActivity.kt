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
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.signature.ObjectKey
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.City
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.service.MyFirebaseMessagingService
import com.openclassrooms.belivre.utils.GlideApp
import com.openclassrooms.belivre.utils.displayNotificationOnDrawer
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.CityViewModel
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import com.openclassrooms.belivre.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.add_media_dialog.view.*
import kotlinx.android.synthetic.main.nav_header.*
import java.io.File
import java.util.*


class ProfileActivity : AppCompatActivity(), LifecycleOwner {

    //////////////////////////////////////////////
    // ---------- MODELS / DATA --------------- //
    //////////////////////////////////////////////

    private var rc: Int? = null
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

    private val userBookVM: UserBookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserBookViewModel() }).get(UserBookViewModel::class.java)
    }

    private var filePath: Uri? = null

    private lateinit var storageReference: StorageReference
    private lateinit var ref: StorageReference

    private var libraryCount : TextView? = null

    private lateinit var addMediaIV:ImageView
    private lateinit var circularProgressDrawable: CircularProgressDrawable
    private lateinit var filePathURI : Uri

    private var mAuth: FirebaseAuth? = null

    private lateinit var mDrawerLayout: DrawerLayout

    //////////////////////////////////////////////
    // ---------- FIREBASE USER --------------- //
    //////////////////////////////////////////////

    private var currentUser : FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = getString(R.string.profile)

        // Retrieving FirebaseAuth instance and current user
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth?.currentUser

        storageReference = FirebaseStorage.getInstance().reference

        ref = FirebaseStorage.getInstance().reference.child("images/profilePictures/${currentUser!!.uid}")

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

        rc = intent.getIntExtra("requestCode", 0)

        // Action Bar --------------------------------------------------
        if(rc != 123) {
            val toolbar: Toolbar = findViewById(R.id.toolbar_profile)
            setSupportActionBar(toolbar)

            val actionbar: ActionBar? = supportActionBar
            actionbar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_menu)
            }

            mDrawerLayout = findViewById(R.id.drawer_layout_profile)

            val navigationView: NavigationView = findViewById(R.id.nav_view_profile)
            navigationView.setNavigationItemSelectedListener { menuItem ->
                // set item as selected to persist highlight
                menuItem.isChecked = true
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers()
                // Handle navigation view item clicks here.
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        val intent = MainActivity.newIntent(this)
                        startActivity(intent)
                    }
                    R.id.nav_setting -> {
                        this.toast(getString(R.string.settings))
                    }
                    R.id.nav_library -> {
                        val intent = LibraryActivity.newIntent(this)
                        intent.putExtra("user", user)
                        startActivity(intent)
                    }
                    R.id.nav_logout ->{
                        mAuth!!.signOut()
                        val intent = SplashActivity.newIntent(this)
                        startActivity(intent)
                    }
                }
                true
            }

            libraryCount = navigationView.menu.findItem(R.id.nav_library).actionView as TextView
        }
        // FAB action setup
        validateProfileFormFAB.setOnClickListener { validateForm() }

        // City field action setup (Google Places API)
        cityET.setOnFocusChangeListener { _, hasFocus ->  if(hasFocus) startAutoCompleteActivity()}

        profilePic.setOnClickListener { createAddMediaDialog() }

        circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
        circularProgressDrawable.start()
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
            user = if(currentUser?.photoUrl != null) User(currentUser?.uid,names?.get(1), names?.get(0), currentUser?.email, "", null, currentUser?.photoUrl.toString())
            else User(currentUser?.uid,names?.get(1), names?.get(0), currentUser?.email, "")
        }
        else{
            user = userRetrived
            if(!user?.cityId.isNullOrEmpty())cityVM.getCity(user?.cityId!!).observe(this, Observer {
                cityET.setText(it!!.name)
                this.city = it
            })
            ref = storageReference.child("images/profilePictures/${this.user!!.id.toString()}")

            if(rc != 123) {

                loadProfilePictureIntoImageView(drawer_imageview_profile, this, user!!.profilePicURL, user!!.id.toString())

                drawer_username.text = getString(R.string.profile_display_name, user!!.firstname, user!!.lastname?.substring(0,1))
                drawer_email.text = user!!.email
            }
            if(libraryCount != null){
                userBookVM.getUserBooksByUserId(user!!.id.toString()).observe(this, Observer { userBooks: List<UserBook>? -> displayNotificationOnDrawer(userBooks, this, this, libraryCount!!) })
            }
        }

        lastNameEt.setText(user!!.lastname)
        firstNameEt.setText(user!!.firstname)
        emailEt.setText(user!!.email)

        displayName.text = getString(R.string.profile_display_name, user!!.firstname, user!!.lastname?.substring(0,1))

        loadProfilePictureIntoImageView(profilePic, this, user!!.profilePicURL, user!!.id.toString())
    }

    //appbar - toolbar button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
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
        if(cityET.text.toString().isNotEmpty() && lastNameEt.text.toString().isNotEmpty() && firstNameEt.text.toString().isNotEmpty()) {
            user?.lastname = lastNameEt.text?.toString()
            user?.firstname = firstNameEt.text?.toString()
            user?.cityId = city?.id
            user?.cityName = city?.name
            cityVM.addCity(city!!)
            userVM.addUser(user!!)
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
                val newToken = instanceIdResult.token
                MyFirebaseMessagingService.addTokenToFirestore(newToken)
                Log.e("newToken", newToken)
            }
            finish()
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
                if (grantResults.isNotEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                    .placeholder(circularProgressDrawable)
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
                if(currentUser!!.photoUrl.toString().isNotEmpty() && currentUser!!.photoUrl != null) {
                    user!!.profilePicURL = currentUser!!.photoUrl.toString()
                    GlideApp.with(this)
                        .load(user!!.profilePicURL)
                        .fitCenter()
                        .placeholder(circularProgressDrawable)
                        .signature(ObjectKey(System.currentTimeMillis().toString()))
                        .circleCrop()
                        .into(profilePic)
                    dialog.dismiss()
                }
                else {
                    user!!.profilePicURL = null
                    profilePic.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_avatar))
                    dialog.dismiss()
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
                .placeholder(circularProgressDrawable)
                .circleCrop()
                .into(addMediaIV)
        }
        if(resultCode == RESULT_OK && requestCode == CAMERA_IMAGE_REQUEST){
            filePath = filePathURI
            GlideApp.with(this)
                .load(filePath)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .fitCenter()
                .placeholder(circularProgressDrawable)
                .circleCrop()
                .into(addMediaIV)
        }
    }

    override fun onBackPressed() {
        rc = intent.getIntExtra("requestCode", 0)
        if(rc == 123){}
        else{
            super.onBackPressed()
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
