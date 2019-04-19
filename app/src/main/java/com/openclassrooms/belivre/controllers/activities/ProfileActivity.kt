package com.openclassrooms.belivre.controllers.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.database.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.database.viewmodels.UserViewModel
import com.openclassrooms.belivre.models.User
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), LifecycleOwner {

    private val vm: UserViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserViewModel() }).get(UserViewModel::class.java)
    }

    private var currentUser : FirebaseUser? = null

    private var names: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val mAuth = FirebaseAuth.getInstance()

        currentUser = mAuth?.currentUser

        vm.getUser(currentUser!!.uid).observe(this, Observer { user:User? -> this.updateUI(user)})

    }

    private fun updateUI(userRetrived: User?){
        val user: User?
        if(userRetrived == null){
            names = currentUser?.displayName?.split(" ")
            user = User(currentUser?.uid,names?.get(1), names?.get(0), currentUser?.email, 0, currentUser?.photoUrl.toString())
            vm.addUser(user)
        }
        else user = userRetrived

        lastNameEt.setText(user.lastname)
        firstNameEt.setText(user.firstname)
        emailEt.setText(user.email)

        displayName.text = getString(R.string.profile_display_name, user.firstname, user.lastname?.substring(0,1))

        Glide.with(this)
            .load(user.profilePicURL)
            .fitCenter()
            .circleCrop()
            .into(profilePic)
    }

    companion object {
        const val TAG = "ProfileActivity"

        fun newIntent(context: Context): Intent {
            return Intent(context, ProfileActivity::class.java)
        }
    }
}
