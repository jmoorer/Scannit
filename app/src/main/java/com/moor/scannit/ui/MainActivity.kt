package com.moor.scannit.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.moor.scannit.R
import com.moor.scannit.RESULT_LOAD_IMAGE
import com.moor.scannit.getRealPathFromUri
import com.moor.scannit.ui.camera.CameraViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val cameraViewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = findNavController(R.id.nav_host)
      //  val drawerLayout= findViewById<DrawerLayout>(R.id.drawer)
        appBarConfiguration = AppBarConfiguration(navController.graph)
//
//        findViewById<NavigationView>(R.id.nav_view)
//            .setupWithNavController(navController)
        setupActionBarWithNavController(navController,appBarConfiguration)

        FirebaseApp.initializeApp(this)


    }

    override fun onSupportNavigateUp(): Boolean {

        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }




}
