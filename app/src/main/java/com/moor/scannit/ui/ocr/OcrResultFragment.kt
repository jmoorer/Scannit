package com.moor.scannit.ui.ocr

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentOcrResultBinding


class OcrResultFragment : Fragment() {


    private lateinit var navController: NavController
    private val args:OcrResultFragmentArgs  by navArgs()
    private val viewModel:OcrViewModel by activityViewModels()

    private val binding: FragmentOcrResultBinding by lazy {
        FragmentOcrResultBinding.bind(requireView())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ocr_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentContainer = view.findViewById<View>(R.id.nav_host)
        navController = Navigation.findNavController(fragmentContainer)
    }
    @SuppressLint("ResourceAsColor")
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

                    bottomNavigation.menu.forEach {
                        val badge = bottomNavigation.getOrCreateBadge(it.itemId)
                        badge.backgroundColor= resources.getColor(R.color.colorAccent)
                        when(it.itemId){
                            R.id.emailFragment->{
                                badge?.isVisible= state.emails.any()
                                badge?.number= state.emails.size
                                it.isEnabled = state.emails.any()
                            }
                            R.id.linkFragment->{
                                badge?.isVisible= state.links.any()
                                badge?.number= state.links.size
                                it.isEnabled = state.links.any()
                            }
                            R.id.phoneNumberFragment->{
                                badge?.isVisible= state.numbers.any()
                                badge?.number= state.numbers.size
                                it.isEnabled = state.numbers.any()
                            }
                            else-> badge.isVisible=false

                        }
                    }
                }

            }
        })
    }


}
