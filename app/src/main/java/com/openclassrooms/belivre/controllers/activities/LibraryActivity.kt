package com.openclassrooms.belivre.controllers.activities

import android.content.Context
import android.content.Intent
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
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.LibraryPagerAdapter
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.utils.displayNotificationOnDrawer
import com.openclassrooms.belivre.utils.loadProfilePictureIntoImageView
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import com.openclassrooms.belivre.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_library.*
import kotlinx.android.synthetic.main.nav_header.view.*

/**
 * Activity used to displays "MyBooks" and "Borrowed" fragments (tabs)
 * @property mDrawerLayout DrawerLayout
 * @property libraryCount TextView
 * @property userBookVM UserBookViewModel
 * @property userVM UserViewModel
 * @property mAuth FirebaseAuth?
 */
class LibraryActivity : AppCompatActivity(), LifecycleOwner {

    // UI
    private lateinit var mDrawerLayout: DrawerLayout

    // DATA
    private lateinit var libraryCount : TextView

    // VIEW MODELS
    private val userBookVM: UserBookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserBookViewModel() }).get(UserBookViewModel::class.java)
    }

    private val userVM: UserViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserViewModel() }).get(UserViewModel::class.java)
    }

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val fragmentAdapter = LibraryPagerAdapter(supportFragmentManager)
        viewpager_library.adapter = fragmentAdapter

        tabs_library.setupWithViewPager(viewpager_library)

        // Used if the activity was started from a notification
        if(intent.hasExtra("tab")){
            viewpager_library.currentItem = intent.getStringExtra("tab").toInt()
        }

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth!!.currentUser

        if (intent.hasExtra("user")) {
            user = intent.getSerializableExtra("user") as User
            updateDrawerUI(user)
        }
        else{
            userVM.getUser(currentUser!!.uid).observe(this, Observer { updateDrawerUI(it) })
        }
    }

    /**
     * Updates the NavigationDrawer UI elements
     * @param user User?
     */
    private fun updateDrawerUI(user: User?){
        if (user != null) {
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
                    R.id.nav_home -> {
                        val intent = MainActivity.newIntent(this)
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

            updateDrawerNavHeader(user, navigationView)

            userBookVM.getUserBooksByUserId(user.id.toString()).observe(this, Observer { userBooks: List<UserBook>? -> displayNotificationOnDrawer(userBooks, this, this, libraryCount) })
        }
    }

    /**
     * Updates the NavigationDrawer's header
     * @param user User
     * @param nav_view NavigationView
     */
    private fun updateDrawerNavHeader(user: User, nav_view :NavigationView){
        nav_view.getHeaderView(0).drawer_username.text = getString(R.string.profile_display_name, user.firstname, user.lastname?.substring(0,1))
        nav_view.getHeaderView(0).drawer_email.text = user.email

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 10f
        circularProgressDrawable.centerRadius = 90f
        circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
        circularProgressDrawable.start()

        loadProfilePictureIntoImageView(nav_view.getHeaderView(0).drawer_imageview_profile, this, user.profilePicURL, user.id.toString())

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
        var user: User? = null
    }
}
