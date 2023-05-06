package com.urwork.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.urwork.mobile.pagers.Explore
import com.urwork.mobile.pagers.Home
import com.urwork.mobile.pagers.Schedule
import com.urwork.mobile.pagers.Tasks
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(Home())
        bottomNav = findViewById(R.id.nav_bar) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    loadFragment(Home())
                    true
                }
                R.id.nav_explore -> {
                    loadFragment(Explore())
                    true
                }
                R.id.nav_schedule -> {
                    loadFragment(Schedule())
                    true
                }
                R.id.nav_tasks -> {
                    loadFragment(Tasks())
                    true
                }
                else -> {
                    loadFragment(Home())
                    true
                }
            }
        }
    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

}