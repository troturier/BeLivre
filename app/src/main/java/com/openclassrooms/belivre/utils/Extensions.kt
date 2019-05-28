package com.openclassrooms.belivre.utils

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.R.id
import com.openclassrooms.belivre.models.UserBook


// 1 - Identifier for Sign-In Activity
const val rcSignIn = 123

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun loadProfilePictureIntoImageView(iv : ImageView, context : Context, profilePicURL : String?, userId: String){
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 10f
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

fun setBadgeCount(context: Context, res: Int, badgeCount: Int): Drawable {
    val icon = ContextCompat.getDrawable(context, R.drawable.ic_badge_drawable) as LayerDrawable?
    val mainIcon = ContextCompat.getDrawable(context, res)
    val badge = BadgeDrawable(context)
    badge.setCount(badgeCount.toString())
    icon!!.mutate()
    icon.setDrawableByLayerId(id.ic_badge, badge)
    icon.setDrawableByLayerId(id.ic_main_icon, mainIcon)

    return icon
}

fun displayNotificationOnDrawer(userBooks:List<UserBook>?, context: Context, appCompatActivity: AppCompatActivity, libraryCount: TextView){
    var alertCount = 0
    if(userBooks != null){
        for (ub in userBooks){
            if(ub.status == 2 || ub.status == 4){
                alertCount++
            }
        }
        appCompatActivity.supportActionBar!!.setHomeAsUpIndicator(setBadgeCount(context, R.drawable.ic_menu, alertCount))
        appCompatActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        appCompatActivity.supportActionBar!!.setDisplayShowCustomEnabled(true)

        libraryCount.gravity = Gravity.CENTER_VERTICAL
        libraryCount.setTypeface(null, Typeface.BOLD)
        libraryCount.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        if (alertCount > 0) {
            libraryCount.text = alertCount.toString()

        }
    }
}



fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}
