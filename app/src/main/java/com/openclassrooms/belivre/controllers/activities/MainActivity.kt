package com.openclassrooms.belivre.controllers.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.Crashlytics
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserViewModel
import io.fabric.sdk.android.Fabric
import com.openclassrooms.belivre.controllers.activities.ProfileActivity as ProfileActivity1


class MainActivity : AppCompatActivity(), LifecycleOwner {


    //FOR DATA
    // 1 - Identifier for Sign-In Activity
    private val rcSignIn = 123

    private var currentUser: FirebaseUser? = null

    private val userVM: UserViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserViewModel() }).get(UserViewModel::class.java)
    }

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth?.currentUser

        if(currentUser == null) startSignInActivity()
        else { userVM.getUser(currentUser!!.uid).observe(this, Observer { user:User? -> checkUserProfileComplete(user)}) }
    }

    private fun checkUserProfileComplete(user: User?){
        if(user == null
            || user.profilePicURL!!.isEmpty()
            || user.cityId!!.isEmpty()
            || user.firstname!!.isEmpty()
            || user.lastname!!.isEmpty()){
            val intent = com.openclassrooms.belivre.controllers.activities.ProfileActivity.newIntent(this)
            intent.putExtra("requestCode", rcSignIn)
            startActivity(intent)
        }
    }

    // 2 - Launch Sign-In Activity
    private fun startSignInActivity() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
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
                // Successfully signed in
                userVM.getUser(currentUser!!.uid).observe(this, Observer { user:User -> checkUserProfileComplete(user)})
            } else {
                startSignInActivity()
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
