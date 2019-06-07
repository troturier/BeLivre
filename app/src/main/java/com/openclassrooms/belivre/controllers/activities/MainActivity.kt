package com.openclassrooms.belivre.controllers.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.crashlytics.android.Crashlytics
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.MainPagerAdapter
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.displayNotificationOnDrawer
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import com.openclassrooms.belivre.utils.toast
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import com.openclassrooms.belivre.viewmodels.UserViewModel
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import com.openclassrooms.belivre.controllers.activities.ProfileActivity as ProfileActivity1


class MainActivity : AppCompatActivity(), LifecycleOwner, AHBottomNavigation.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    //FOR DATA
    private var currentUser: FirebaseUser? = null

    private lateinit var toolbar: Toolbar

    private lateinit var libraryCount : TextView

    private lateinit var navigationView: NavigationView

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

        // -------------------------------------------------------------
        mAuth = FirebaseAuth.getInstance()

        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth?.currentUser

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.nav_view)
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
                    val intent = SplashActivity.newIntent(this)
                    startActivity(intent)
                }
            }
            true
        }

        libraryCount = navigationView.menu.findItem(R.id.nav_library).actionView as TextView

        userVM.getUser(currentUser!!.uid).observe(this, Observer { checkUserProfileComplete(it)})

        configureBottomNav()
    }

    private fun configureBottomNav(){
        val mapItem = AHBottomNavigationItem(getString(R.string.map), ContextCompat.getDrawable(this, R.drawable.ic_map), ContextCompat.getColor(this, R.color.colorPrimaryDark))
        val listItem = AHBottomNavigationItem(getString(R.string.list), ContextCompat.getDrawable(this, R.drawable.ic_list), ContextCompat.getColor(this, R.color.colorPrimary))
        val chatItem = AHBottomNavigationItem(getString(R.string.chat), ContextCompat.getDrawable(this, R.drawable.ic_chat), ContextCompat.getColor(this, R.color.colorAccent))

        bottom_navigation_main.addItem(mapItem)
        bottom_navigation_main.addItem(listItem)
        bottom_navigation_main.addItem(chatItem)

        bottom_navigation_main.defaultBackgroundColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        bottom_navigation_main.accentColor = ContextCompat.getColor(this, R.color.colorAccent)
        bottom_navigation_main.inactiveColor = Color.WHITE

        bottom_navigation_main.isColored = true

        bottom_navigation_main.setOnTabSelectedListener(this)

        bottom_navigation_main.currentItem = 0

        val adapter = MainPagerAdapter(supportFragmentManager)
        viewPager_main.adapter = adapter

        viewPager_main.addOnPageChangeListener(this)
        viewPager_main.offscreenPageLimit = 2
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
        if (user2 != null) {
            user = user2

            navigationView.getHeaderView(0).drawer_username.text = getString(R.string.profile_display_name, user2.firstname, user2.lastname?.substring(0,1))
            navigationView.getHeaderView(0).drawer_email.text = user2.email
            loadProfilePictureIntoImageView(navigationView.getHeaderView(0).drawer_imageview_profile, this, user2.profilePicURL, user2.id.toString())

            userBookVM.getUserBooksByUserId(user.id.toString()).observe(this, Observer { userBooks: List<UserBook>? -> displayNotificationOnDrawer(userBooks, this, this, libraryCount)})
        }
    }

    override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
        when(position){
            0 -> {
                viewPager_main.currentItem = 0
                toolbar.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            }
            1 -> {
                viewPager_main.currentItem = 1
                toolbar.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
            }
            else -> {
                viewPager_main.currentItem = 2
                toolbar.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent))
            }
        }
        return true
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        bottom_navigation_main.currentItem = position
    }

    override fun onBackPressed() {

    }

    companion object {
        const val TAG = "MainActivity"

        fun newIntent(context: Context): Intent {
            return Intent(context, com.openclassrooms.belivre.controllers.activities.MainActivity::class.java)
        }
        lateinit var user : User
    }
}
