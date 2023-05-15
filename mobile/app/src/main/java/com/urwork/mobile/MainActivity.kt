package com.urwork.mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.urwork.mobile.pagers.Explore
import com.urwork.mobile.pagers.Home
import com.urwork.mobile.pagers.Schedule
import com.urwork.mobile.pagers.Tasks
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gotoAdd: FloatingActionButton = findViewById(R.id.fab)
        gotoAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, EditProject::class.java))
        }

        supportActionBar!!.setDisplayShowHomeEnabled(false)

        bottomNav = findViewById<BottomNavigationView>(R.id.nav_bar)

        loadFragment(Home(), R.id.nav_home)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    loadFragment(Home(), R.id.nav_home)
                    true
                }
                R.id.nav_explore -> {
                    loadFragment(Explore(), R.id.nav_explore)
                    true
                }
                R.id.nav_schedule -> {
                    loadFragment(Schedule(), R.id.nav_schedule)
                    true
                }
                R.id.nav_tasks -> {
                    loadFragment(Tasks(), R.id.nav_tasks)
                    true
                }
                else -> {
                    loadFragment(Home(), R.id.nav_home)
                    true
                }
            }
        }
    }

    fun loadFragment(fragment: Fragment, itemId: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()

        // Mengubah item aktif
        val menuItem: MenuItem? = bottomNav.menu.findItem(itemId)
        menuItem?.setChecked(true)
    }

}