package com.openclassrooms.belivre.controllers.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.navigation.NavigationView
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.LibraryPagerAdapter
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.utils.GlideApp
import com.openclassrooms.belivre.utils.toast
import kotlinx.android.synthetic.main.activity_library.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*

class LibraryActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val fragmentAdapter = LibraryPagerAdapter(supportFragmentManager)
        viewpager_library.adapter = fragmentAdapter

        tabs_library.setupWithViewPager(viewpager_library)

        user = intent.getSerializableExtra("user") as User

        updateDrawerUI()
    }

    private fun updateDrawerUI(){
        val toolbar: Toolbar = this.findViewById(R.id.toolbar_library)
        setSupportActionBar(toolbar)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout_library)

        val navigationView: NavigationView = findViewById(R.id.nav_view_library)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()
            // Handle navigation view item clicks here.
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    val intent = ProfileActivity.newIntent(this)
                    startActivity(intent)
                }
                R.id.nav_setting -> {
                    this.toast(getString(R.string.settings))
                }
                R.id.nav_home -> {
                    val intent = MainActivity.newIntent(this)
                    startActivity(intent)
                }
            }
            true
        }
        updateDrawerNavHeader(user, navigationView)
    }

    private fun updateDrawerNavHeader(user: User, nav_view :NavigationView){
        nav_view.getHeaderView(0).drawer_username.text = getString(R.string.profile_display_name, user.firstname, user.lastname?.substring(0,1))
        nav_view.getHeaderView(0).drawer_email.text = user.email

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 90f
        circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
        circularProgressDrawable.start()

        val ref = FirebaseStorage.getInstance().reference.child("images/profilePictures/${user.id.toString()}")

        when {
            user.profilePicURL.equals("images/profilePictures/${user.id.toString()}") -> GlideApp.with(this)
                .load(ref)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .placeholder(circularProgressDrawable)
                .fitCenter()
                .circleCrop()
                .into(drawer_imageview_profile)
            user.profilePicURL!!.isNotEmpty() -> GlideApp.with(this)
                .load(user.profilePicURL)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .placeholder(circularProgressDrawable)
                .fitCenter()
                .circleCrop()
                .into(nav_view.getHeaderView(0).drawer_imageview_profile)
            else -> {
                user.profilePicURL = ""
                GlideApp.with(this)
                    .load(R.drawable.ic_avatar)
                    .signature(ObjectKey(System.currentTimeMillis().toString()))
                    .placeholder(circularProgressDrawable)
                    .fitCenter()
                    .circleCrop()
                    .into(nav_view.getHeaderView(0).drawer_imageview_profile)
            }
        }
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

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, LibraryActivity::class.java)
        }
        lateinit var user: User
    }
}
