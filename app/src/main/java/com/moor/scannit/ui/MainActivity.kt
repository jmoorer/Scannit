package com.moor.scannit.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.moor.scannit.R

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = findNavController(R.id.nav_host)
        val drawerLayout= findViewById<DrawerLayout>(R.id.drawer)
        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout)

        findViewById<NavigationView>(R.id.nav_view)
            .setupWithNavController(navController)
        setupActionBarWithNavController(navController,appBarConfiguration)

        FirebaseApp.initializeApp(this)


    }

    override fun onSupportNavigateUp(): Boolean {

        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
