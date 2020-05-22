package com.moor.scannit.ui.preview

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentImageBinding


class ImageFragment : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding=FragmentImageBinding.inflate(inflater,container,false)
        arguments?.getString("image")?.let {
            binding.imageView.setImageURI(Uri.parse(it))
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(uri: String) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putString("image", uri)
                }
            }
    }
}
