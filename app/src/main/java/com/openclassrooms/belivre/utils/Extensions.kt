package com.openclassrooms.belivre.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.belivre.R

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun loadProfilePictureIntoImageView(iv : ImageView, context : Context, profilePicURL : String?, userId: String){
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 10f
    circularProgressDrawable.centerRadius = 90f
    circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
    circularProgressDrawable.start()

    val ref = FirebaseStorage.getInstance().reference.child("images/profilePictures/$userId")

    when {
        profilePicURL.equals("images/profilePictures/$userId") -> GlideApp.with(context)
            .load(ref)
            .signature(ObjectKey(System.currentTimeMillis().toString()))
            .placeholder(circularProgressDrawable)
            .fitCenter()
            .circleCrop()
            .into(iv)
        profilePicURL != null ->
            if(profilePicURL.isNotEmpty() ) {
            GlideApp.with(context)
                .load(profilePicURL)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .placeholder(circularProgressDrawable)
                .fitCenter()
                .circleCrop()
                .into(iv)
        }
    }
}
