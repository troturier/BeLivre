package com.openclassrooms.belivre.controllers.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.belivre.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth?.currentUser

        val names = currentUser?.displayName?.split(" ")

        lastNameEt.setText(names?.get(1))
        firstNameEt.setText(names?.get(0))
        emailEt.setText(currentUser?.email)

        displayName.text = getString(R.string.profile_display_name, names?.get(0), names?.get(1)?.substring(0,1))

        Glide.with(this)
            .load(currentUser?.photoUrl)
            .fitCenter()
            .circleCrop()
            .into(profilePic)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }
}
