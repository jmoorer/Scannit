package com.moor.scannit.ui.camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.isseiaoki.simplecropview.callback.LoadCallback

import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentImageProccessBinding


class ImageProccessFragment : Fragment(), LoadCallback {

    private lateinit var binding: FragmentImageProccessBinding
    private  val args:ImageProccessFragmentArgs by  navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentImageProccessBinding.inflate(inflater,container,false)
        binding.cropImageView.load(args.uri).execute(this)
        return binding.root
    }

    override fun onSuccess() {
        //TODO("Not yet implemented")
    }

    override fun onError(e: Throwable?) {
        //TODO("Not yet implemented")
    }


}
