package co.id.gmedia.yia

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import co.id.gmedia.yia.ui.gallery.GalleryFragment
import co.id.gmedia.yia.ui.home.HomeFragment
import co.id.gmedia.yia.ui.share.ShareFragment
import co.id.gmedia.yia.ui.slideshow.SlideshowFragment
import co.id.gmedia.yia.ui.tools.ToolsFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var context: Context? = null
    private val mAppBarConfiguration: AppBarConfiguration? = null
    private var state = 0
    private val TAG = "data"
    private var exitState = false
    private val timerClose = 2000
    private var navigationView: NavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        context = this
        setSupportActionBar(toolbar)

        //Check close statement
        doubleBackToExitPressedOnce = false
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getBoolean("exit", false)) {
                exitState = true
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        title = "Home"
        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView!!.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    private fun ChangeFragment(stateChecked: Int) {
        var position = 0
        val item = navigationView!!.menu.findItem(stateChecked)
        item.isCheckable = true
        item.isChecked = true
        when (stateChecked) {
            R.id.nav_home -> {
                title = "Home"
                fragment = HomeFragment()
                position = 0
            }
            R.id.nav_gallery -> {
                title = "Gallery"
                fragment = GalleryFragment()
                position = 1
            }
            R.id.nav_share -> {
                title = "Riwayat Penjualan"
                fragment = ShareFragment()
                position = 2
            }
            R.id.nav_slideshow -> {
                title = "Promo"
                fragment = SlideshowFragment()
                position = 3
            }
            else -> {
                title = "Home"
                fragment = ToolsFragment()
                position = 4
            }
        }
        if (position > state) {
            callFragment(context, fragment)
        } else if (position < state) {
            callFragmentBack(context, fragment)
        }
        state = position
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val id = menuItem.itemId
        Log.d(TAG, "onNavigationItemSelected: ")
        ChangeFragment(id)
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (state != 0) {
                ChangeFragment(R.id.nav_home)
            } else {
                val builder = AlertDialog.Builder(context!!)
                val inflater = (context as Activity?)!!.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val viewDialog = inflater.inflate(R.layout.layout_exit_dialog, null)
                builder.setView(viewDialog)
                builder.setCancelable(false)
                val btnYa = viewDialog.findViewById<View>(R.id.btn_ya) as Button
                val btnTidak = viewDialog.findViewById<View>(R.id.btn_tidak) as Button
                val alert = builder.create()
                alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                btnYa.setOnClickListener {
                    alert?.dismiss()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("exit", true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
                btnTidak.setOnClickListener { alert?.dismiss() }
                alert.show()
            }
        }
    }

    companion object {
        private var doubleBackToExitPressedOnce = false
        private var fragment: Fragment? = null
        private fun callFragment(context: Context?, fragment: Fragment?) {
            (context as AppCompatActivity?)!!.supportFragmentManager
                    .beginTransaction() //.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                    //.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                    .replace(R.id.nav_host_fragment, fragment!!, fragment.javaClass.simpleName)
                    .addToBackStack(null)
                    .commit()
        }

        private fun callFragmentBack(context: Context?, fragment: Fragment?) {
            (context as AppCompatActivity?)!!.supportFragmentManager
                    .beginTransaction() //.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
                    //.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                    .replace(R.id.nav_host_fragment, fragment!!, fragment.javaClass.simpleName)
                    .addToBackStack(null)
                    .commit()
        }
    }
}