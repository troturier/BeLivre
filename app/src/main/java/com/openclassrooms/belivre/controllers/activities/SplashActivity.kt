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

class SplashActivity : AppCompatActivity(), LifecycleOwner {

    private val userVM: UserViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserViewModel() }).get(UserViewModel::class.java)
    }

    private var currentUser: FirebaseUser? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // -------------------------------------------------------------
        mAuth = FirebaseAuth.getInstance()

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth?.currentUser

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
