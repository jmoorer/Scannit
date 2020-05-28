package com.moor.scannit.ui.camera

import android.graphics.Bitmap
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
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentImageProcessBinding

import com.uvstudio.him.photofilterlibrary.PhotoFilter
import java.io.File
import java.io.FileOutputStream
import java.net.URI


class ImageProcessFragment : Fragment(){

    private lateinit var binding: FragmentImageProcessBinding
    private val viewModel:CameraViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getImage().observe(viewLifecycleOwner, Observer {bitmap->
            binding.apply {
                imageView.setImageBitmap(bitmap)
                doneButton.setOnClickListener {
                     bitmap?.let{
                        val documentId=   viewModel.saveCroppedImage(bitmap)
                        val action= ImageProcessFragmentDirections.actionImageProccessFragmentToDocumentFragment(documentId)
                        findNavController().navigate(action)
                     }
                }

                ocrButton.setOnClickListener {

                    findNavController().navigate(R.id.imageFilterFragment)
//                    val temp=File.createTempFile("images",".jpg",requireContext().cacheDir)
//                    val output = FileOutputStream(temp)
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
//                    output.flush()
//                    output.close()
//                    findNavController().navigate(R.id.extractedTextFragment, bundleOf("image_uri" to Uri.fromFile(temp).toString() ))
                }

            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentImageProcessBinding.inflate(inflater,container,false).apply {

            cropButton.setOnClickListener {
                findNavController().navigate(R.id.cropFragment)
            }



        }


        return binding.root
    }

}
