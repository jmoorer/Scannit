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
    private val binding:FragmentCropBinding by lazy {
        FragmentCropBinding.bind(requireView())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_crop, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getImage().observe(viewLifecycleOwner, Observer { bitmap->
            binding.apply {
                cropImageView.setImageBitmap(bitmap)

                doneButton.setOnClickListener {
                    viewModel.setImage(cropImageView.croppedImage)
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
