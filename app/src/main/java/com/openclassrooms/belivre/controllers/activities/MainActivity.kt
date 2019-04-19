package com.openclassrooms.belivre.controllers.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.belivre.R
import io.fabric.sdk.android.Fabric
import com.openclassrooms.belivre.controllers.activities.ProfileActivity as ProfileActivity1


class MainActivity : AppCompatActivity() {


    //FOR DATA
    // 1 - Identifier for Sign-In Activity
    private val rcSignIn = 123

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.currentUser

        if(currentUser == null) startSignInActivity()
        else {
            updateUI(currentUser)
            val intent = com.openclassrooms.belivre.controllers.activities.ProfileActivity.newIntent(this)
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

    private fun updateUI(user: FirebaseUser){
        val displayName = findViewById<TextView>(R.id.username)
        val email = findViewById<TextView>(R.id.email)

        displayName.text = user.displayName
        email.text = user.email
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
                updateUI(user)
                val intent = com.openclassrooms.belivre.controllers.activities.ProfileActivity.newIntent(this)
                startActivity(intent)
            } else {
                startSignInActivity()
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
