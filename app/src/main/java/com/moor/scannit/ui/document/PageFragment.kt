package com.moor.scannit.ui.document

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentPageBinding

class PageFragment:Fragment() {

    private lateinit var binding: FragmentPageBinding
    val viewModel:DocumentViewModel by activityViewModels()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPageBinding.inflate(inflater,container,false).apply {}
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getDocument().observe(viewLifecycleOwner, Observer { doc->
            binding.apply {
                val pagerAdapter = PageAdapter(doc.pages)
                pagesViewPager.adapter= pagerAdapter
                pageAddButton.setOnClickListener {
                    val action = PageFragmentDirections.actionPageFragmentToCameraFragment(doc.id)
                    findNavController().navigate(action)
                }
                deleteButton.setOnClickListener {
                    val page=  doc.pages[pagesViewPager.currentItem]
                    viewModel.removePage(page)
                    pagerAdapter.notifyDataSetChanged()
                    findNavController().popBackStack()
                }
                ocrButton.setOnClickListener {
                    val page=  doc.pages[pagesViewPager.currentItem]
                    val action= PageFragmentDirections.actionPageFragmentToOcrFragment(Uri.parse(page.uri))
                    findNavController().navigate(action)
                }
            }
        })

    }
}