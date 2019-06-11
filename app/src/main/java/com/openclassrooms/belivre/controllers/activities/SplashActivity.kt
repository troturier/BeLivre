package com.openclassrooms.belivre.controllers.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.service.MyFirebaseMessagingService
import com.openclassrooms.belivre.utils.rcSignIn
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserViewModel

/**
 * Splash Activity used when launching the application
 * @property userVM UserViewModel
 * @property currentUser FirebaseUser?
 * @property mAuth FirebaseAuth?
 */
class SplashActivity : AppCompatActivity(), LifecycleOwner {

    // VIEW MODEL
    private val userVM: UserViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserViewModel() }).get(UserViewModel::class.java)
    }

    // FIREBASE
    private var currentUser: FirebaseUser? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth?.currentUser

        // If the user is not signed -> Start the Sign In Activity
        if(currentUser == null) startSignInActivity(this)
        else {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
                val newToken = instanceIdResult.token
                MyFirebaseMessagingService.addTokenToFirestore(newToken)
                Log.e("newToken", newToken)
            }
            userVM.getUser(currentUser!!.uid).observe(this, Observer { user: User? -> checkUserProfileComplete(user)})
        }
    }

    /**
     * Starts the Firebase Sign In Activity
     * @param activity AppCompatActivity
     */
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
            123
        )
    }

    /**
     * Ensures that the current user's profile is complete
     * if not, start the ProfileActivity
     * @param user2 User?
     */
    private fun checkUserProfileComplete(user2: User?){
        if(user2?.cityId == null || user2.cityId?.isEmpty()!!
            || user2.firstname == null || user2.firstname?.isEmpty()!!
            || user2.lastname == null || user2.lastname?.isEmpty()!!){
            val intent = ProfileActivity.newIntent(this)
            intent.putExtra("requestCode", rcSignIn)
            startActivity(intent)
        }
        else{
            val intent = MainActivity.newIntent(this)
            finish()
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
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
        fun newIntent(context: Context): Intent {
            return Intent(context, com.openclassrooms.belivre.controllers.activities.SplashActivity::class.java)
        }
    }
}
