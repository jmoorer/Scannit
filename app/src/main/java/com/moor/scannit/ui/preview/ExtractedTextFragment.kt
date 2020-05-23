package com.moor.scannit.ui.preview

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.moor.scannit.R
import com.moor.scannit.databinding.FragmentExtractedTextBinding
import com.moor.scannit.ui.SpacesItemDecoration


class ExtractedTextFragment : Fragment() {

    private lateinit var image: FirebaseVisionImage
    private lateinit var detector: FirebaseVisionTextRecognizer
    private lateinit var adapter: ExtractedTextRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val uri = it.getString(IMAGE_URI)
            image = FirebaseVisionImage.fromFilePath(requireContext(), Uri.parse(uri))
            detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding= FragmentExtractedTextBinding.inflate(inflater,container, false)

        detector.processImage(image)
            .addOnSuccessListener { result ->
                binding.extractedTextEditText.setText(result.textBlocks.joinToString("\n") { b -> b.text })
            }
            .addOnFailureListener { e ->
                print("")
            }

        return binding.root
    }






    companion object {

        // TODO: Customize parameter argument names
        const val IMAGE_URI = "image_uri"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(uri:String) =
            ExtractedTextFragment().apply {
                arguments = Bundle().apply {
                   putString(IMAGE_URI,uri)
                }
            }
    }
}
