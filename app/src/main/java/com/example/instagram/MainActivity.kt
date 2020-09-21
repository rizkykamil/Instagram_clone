package com.example.instagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.instagram.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation.setOnNavigationItemSelectedListener(onBottomNavigationListener)


//        var frag = supportFragmentManager.beginTransaction()
//        frag.add(R.id.itemhome, HomeFragment())
//        frag.commit()
    }

    private val onBottomNavigationListener =
        BottomNavigationView.OnNavigationItemSelectedListener { a ->

            var selectedFragment: Fragment = HomeFragment()

            when (a.itemId) {
                R.id.itemhome -> {
                    selectedFragment = HomeFragment()
                }
                R.id.explor -> {
                    selectedFragment = SearchFragment()

                }
                R.id.profile -> {
                    selectedFragment = ProfileFragment()

                }

                R.id.activity -> {
                    selectedFragment = ActivityFragment()

                }


                R.id.camera -> {
                    a.isChecked = false
                    startActivity(Intent(this@MainActivity,AddPostActivity::class.java))
                    return@OnNavigationItemSelectedListener true

                }

            }

            var frag = supportFragmentManager.beginTransaction()
            frag.replace(R.id.framelayout, selectedFragment)
            frag.commit()

            true

        }
}
