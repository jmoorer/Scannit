package com.moor.scannit.ui.camera

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.LoadCallback

import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentImageProcessBinding
import com.moor.scannit.ui.preview.PreviewActivity
import java.io.File
import java.io.FileOutputStream


class ImageProcessFragment : Fragment(){

    private lateinit var binding: FragmentImageProcessBinding
    private val viewModel:CameraViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getImage().observe(viewLifecycleOwner, Observer {bitmap->
            binding.cropImageView.apply {
                imageBitmap=bitmap
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentImageProcessBinding.inflate(inflater,container,false).apply {
            cropImageView.apply {
                setCropMode(CropImageView.CropMode.FREE)
            }
            rotateLeftButton.setOnClickListener {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D)
            }

            rotateRightButton.setOnClickListener {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D)
            }

            cropButton.setOnClickListener {
                val bmp=cropImageView.croppedBitmap
                val documentId=viewModel.saveCroppedImage(bmp)
                val action= ImageProcessFragmentDirections.actionImageProccessFragmentToDocumentFragment(documentId)
                findNavController().navigate(action)
            }
        }


        return binding.root
    }

}
