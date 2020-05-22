package com.moor.scannit.ui.home


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.internal.NavigationMenu
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentHomeBinding
import com.moor.scannit.ui.ScanActivity
import io.github.yavski.fabspeeddial.FabSpeedDial


class HomeFragment : Fragment(), FabSpeedDial.MenuListener {


    private var isFABOpen: Boolean= false
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        binding.actionButton.setMenuListener(this)
        return binding.root
    }

    override fun onPrepareMenu(menu: NavigationMenu?): Boolean {
       // TODO("Not yet implemented")
        return true
    }

    override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
        when(menuItem?.itemId){

            R.id.action_scan-> {
//                val intent = Intent(activity, ScanActivity::class.java)
//                startActivity(intent)
                findNavController().navigate(R.id.cameraFragment)
            }
        }
        return false
    }

    override fun onMenuClosed() {
        //TODO("Not yet implemented")
    }


}
