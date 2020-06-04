package com.moor.scannit.ui.camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toRect
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController


import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentCropBinding


class CropFragment : Fragment() {

    private val viewModel:CameraViewModel by activityViewModels()
    private lateinit var binding:FragmentCropBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCropBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getState().observe(viewLifecycleOwner, Observer {state->
            binding.apply {
                cropImageView.setImageBitmap(state.originalBitmap)

                doneButton.setOnClickListener {
                    viewModel.setCropped(cropImageView.croppedImage)
                    findNavController().navigate(R.id.imageFilterFragment)
                }
                rotateLeftButton.setOnClickListener {
                    cropImageView.rotateImage(-90)
                }

                rotateRightButton.setOnClickListener {
                    cropImageView.rotateImage(90)
                }

                resetButton.setOnClickListener {
                    cropImageView.resetCropRect()
                }

            }
        })
    }


}
