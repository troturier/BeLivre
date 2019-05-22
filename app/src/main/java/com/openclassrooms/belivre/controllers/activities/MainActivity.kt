package com.openclassrooms.belivre.controllers.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.Crashlytics
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.displayNotificationOnDrawer
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import com.openclassrooms.belivre.utils.rcSignIn
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import com.openclassrooms.belivre.viewmodels.UserViewModel
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.nav_header.*
import com.openclassrooms.belivre.controllers.activities.ProfileActivity as ProfileActivity1


class MainActivity : AppCompatActivity(), LifecycleOwner {


    //FOR DATA

    private var currentUser: FirebaseUser? = null

    private lateinit var toolbar: Toolbar

    private lateinit var user : User

    private val userVM: UserViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserViewModel() }).get(UserViewModel::class.java)
    }

    private val userBookVM: UserBookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserBookViewModel() }).get(UserBookViewModel::class.java)
    }

    private var mAuth: FirebaseAuth? = null

    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)

        // Action Bar --------------------------------------------------

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)



        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()
            // Handle navigation view item clicks here.
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    val intent = com.openclassrooms.belivre.controllers.activities.ProfileActivity.newIntent(this)
                    startActivity(intent)
                }
                R.id.nav_setting -> {
                    this.toast(getString(R.string.settings))
                }
                R.id.nav_library -> {
                    val intent = LibraryActivity.newIntent(this)
                    intent.putExtra("user",user)
                    startActivity(intent)
                }
                R.id.nav_logout ->{
                    mAuth!!.signOut()
                    startSignInActivity(this)
                }
            }
            true
        }

        // -------------------------------------------------------------

        mAuth = FirebaseAuth.getInstance()

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth?.currentUser

        if(currentUser == null) startSignInActivity(this)
        else { userVM.getUser(currentUser!!.uid).observe(this, Observer { user:User? -> checkUserProfileComplete(user)}) }
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

    private fun checkUserProfileComplete(user2: User?){
        if(user2 == null
            || user2.cityId!!.isEmpty()
            || user2.firstname!!.isEmpty()
            || user2.lastname!!.isEmpty()){
            val intent = com.openclassrooms.belivre.controllers.activities.ProfileActivity.newIntent(this)
            intent.putExtra("requestCode", rcSignIn)
            startActivity(intent)
        }
        else{
            user = user2

            drawer_username.text = getString(R.string.profile_display_name, user2.firstname, user2.lastname?.substring(0,1))
            drawer_email.text = user2.email
            loadProfilePictureIntoImageView(drawer_imageview_profile, this, user2.profilePicURL, user2.id.toString())

            userBookVM.getUserBooksByUserId(user.id.toString()).observe(this, Observer { userBooks: List<UserBook>? -> displayNotificationOnDrawer(userBooks, this, this)})
        }
    }


    private fun startSignInActivity(activity: AppCompatActivity) {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build())

        // Create and launch sign-in intent
        activity.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.FirebaseAuthUiCustom)
                .setLogo(R.drawable.logo)
                .setIsSmartLockEnabled(false)
                .build(),
            rcSignIn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            if (resultCode == Activity.RESULT_OK) {
                currentUser = mAuth?.currentUser
                // Successfully signed in
                userVM.getUser(currentUser!!.uid).observe(this, Observer { user:User? -> checkUserProfileComplete(user)})
            } else {
                startSignInActivity(this)
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"

        fun newIntent(context: Context): Intent {
            return Intent(context, com.openclassrooms.belivre.controllers.activities.MainActivity::class.java)
        }
    }
}
