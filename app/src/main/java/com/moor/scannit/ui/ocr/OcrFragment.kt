package com.moor.scannit.ui.ocr

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentImageFilterBinding
import com.moor.scannit.databinding.FragmentOcrBinding


class OcrFragment : Fragment() {


    private lateinit var navController: NavController
    private val args:OcrFragmentArgs  by navArgs()
    private val viewModel:OcrViewModel by activityViewModels()

    private val binding: FragmentOcrBinding by lazy {
        FragmentOcrBinding.bind(requireView())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ocr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentContainer = view.findViewById<View>(R.id.nav_host)
        navController = Navigation.findNavController(fragmentContainer)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val dialog=LoadingDialog(requireContext())
        val image=FirebaseVisionImage.fromFilePath(requireContext(),args.uri)

        viewModel.extractText(image).observe(viewLifecycleOwner, Observer { state->
            if(state.isLoading)
                dialog.startLoadingDialog()
            else{
                dialog.stopLoadingDialog()
                binding.apply {
                    bottomNavigation.setupWithNavController(navController)
                }

            }


        })


    }


}
